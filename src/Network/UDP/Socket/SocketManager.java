package Network.UDP.Socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ThreadTools.ThreadControl;
import Network.UDP.Packet.Connect;
import Network.UDP.Packet.Message;
import Network.UDP.Packet.UDP_Packet;
import Network.UDP.Packet.UDP_Packet.Type;
import Network.UDP.TransferProtocol.*;
import Shared.Crypt;

/**
 * Class responsible for handling UDP Socket's IO and answering to some methods (?)
 */
public class SocketManager
{   
    public class RePacket
    {
        public long added_time;                             //time at which the packet was added to the queue
        UDP_Packet packet;                                  //packet to transmit
    }
    public class Connection
    {
        final int MAX_LATENCY= 300;
        final int INITIAL_PACKETS= 10;

        ReentrantReadWriteLock rwl;
        KeyPair keys;
        Crypt crypt;
        Map<Long,Long> user_destination;                            // destination for a user's packets
        long window;                                                // sets the availability to trasnmit "window" packets in a given moment
        Map<Long, RePacket> retransmission_packets;                 // packet number to packet to retransmit
        BlockingQueue<UDP_Packet> transmission_packets;             // packets to transmit
        Map<Long, BlockingQueue<TransferPacket>> received_packets;  // users to packets received
        long inc;                                                   // number of the next packet to send
        long received_number;                                       // highest number of packet received
        List<Long> non_received;                                    // list of packets with number lower than "received number" that we haven't received
        
        Connection (KeyPair keys)
        {
            this.rwl= new ReentrantReadWriteLock();
            this.keys= keys;
            this.crypt= null;
            this.user_destination= new HashMap<>();
            this.window= INITIAL_PACKETS;
            this.transmission_packets= new LinkedBlockingQueue<>();
            this.retransmission_packets= new HashMap<>();
            this.inc= 0;
            this.received_number= 0;
            this.non_received= new ArrayList<>();
            
            //add a connection message to the sender;
            this.transmission_packets.add(new Connect(new UDP_Packet(Type.CON, 0, 0, inc++), keys.getPublic()));
        }

        Connection (KeyPair keys, Crypt crypt)
        {
            this(keys);
            this.crypt= crypt;
        }

        /**
         * registers user with no destination info
         */
        void registerUser (long user)
        {
            registerUser(user, 0);
        }

        /**
         * Registers a user with a given destination
         * @param user user's id
         * @param dest destination's id
         */
        void registerUser (long user, long dest)
        {
            try
            {
                this.rwl.writeLock().lock();
                
                this.user_destination.put(user, dest);
                BlockingQueue<TransferPacket> queue= new LinkedBlockingQueue<>();
                this.received_packets.put(user, queue);
            }
            finally
            {
                this.rwl.writeLock().unlock();
            }
        }

        /**
         * method to be used by users to get their input packet queue
         * @param user user's id
         * @return user's input packet queue
         */
        BlockingQueue<TransferPacket> getInput (long user)
        {
            try
            {
                this.rwl.readLock().lock();
                return this.received_packets.get(user);
            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }

        /**
         * Adds a packet to the send queue
         */
        void addPacketTransmission (long user_id, TransferPacket transfer_packet) throws Exception
        {
            try
            {
                //encrypt payload
                byte [] encrypted_payload= this.crypt.encrypt(transfer_packet.serialize());
                
                //create packet
                long from= user_id;
                long to= this.user_destination.get(user_id);
                UDP_Packet udp_packet= new Message(new UDP_Packet(Type.MSG, from, to, this.inc++), encrypted_payload);
             
                //lock
                this.rwl.writeLock().lock();

                //add packet to transmission queue
                this.transmission_packets.add(udp_packet);
            }
            finally
            {
                this.rwl.writeLock().unlock();
            }
        }

        //method to be used when a packet is received
        void received (UDP_Packet packet)
        {

        }

        /**
         * Method to send a single packet
         * @param socket socket to use
         * @param packet packet to send
         * @param now timestamp for the moment we sent the packet
         */
        private void send (DatagramSocket socket, UDP_Packet packet, long now)
        {

        }

        /**
         * Method to be called by the sender to send every possible packet in accordance with the connection's own state
         * @param socket datagram socket we are using in the socket manager
         * @param target_address target's address
         * @param target_port target port
         */
        void sendALL (DatagramSocket socket, InetAddress target_address, long target_port)
        {
            try
            {
                this.rwl.writeLock().lock();

                //get current timestamp
                long now= System.currentTimeMillis();

                //send retransmission packets
                while (this.window!= 0)
                {
                    for (RePacket repacket: this.retransmission_packets.values())
                    {
                        //Consider the packet lost if a packet is in the retransmission queue for more than "MAX_LATENCY" else we dont send it and wait for the Ack
                        if (now- repacket.added_time< MAX_LATENCY)
                        {
                            send (socket, repacket.packet, now);
                            //decrement the window to reflect the sent packet
                            this.window--;
                        }
                    }
                }
                //send other packets
                while (this.window!= 0)
                {
                    List<UDP_Packet> packets_list= new ArrayList<>();
                    this.transmission_packets.drainTo(packets_list);

                    for (UDP_Packet packet: packets_list)
                    {
                        send (socket, packet, now);
                        //decrement the window to reflect the sent packet
                        this.window--;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                this.rwl.writeLock().unlock();
            }
        }
    }
    public class UserData
    {
        public long user_id;
        public Connection user_connection;
        
        UserData (Connection c, long user_id)
        {
            this.user_connection= c;
            this.user_id= user_id;
        }
    }
    
    
    ReentrantReadWriteLock rwl;
    //Used to uniquely identify each user
    private static long inc= 0;                                                 
    KeyPair keys;
    Map<InetAddress, Connection> address_to_connection;
    Map<Long,Connection> user_to_connection;

    SocketManager ()
    {
        try
        {
            this.rwl= new ReentrantReadWriteLock();
            this.keys= Crypt.generateKeyPair();
            this.address_to_connection= new HashMap<>();
            this.user_to_connection= new HashMap<>();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    UserData registerUser (InetAddress adr)
    {
        try
        {
            this.rwl.writeLock().lock();
            long user_id= inc++;

            // get user's connection
            Connection c;
            if ((c= this.address_to_connection.get(adr))== null)
            {
                c= new Connection(keys);
            }
            this.address_to_connection.put(adr, c);

            // register user in the connection
            c.registerUser(user_id);
            
            // relate user to the connection
            this.user_to_connection.put(user_id, c);

            return new UserData(c, user_id);
        }
        finally
        {
            this.rwl.writeLock().unlock();
        }
    }
}
