package Network.UDP.Socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Blocker.FileBlockInfo;
import Network.UDP.Packet.Ack;
import Network.UDP.Packet.Connect;
import Network.UDP.Packet.Message;
import Network.UDP.Packet.UDP_Packet;
import Network.UDP.Packet.UDP_Packet.Type;
import Network.UDP.TransferProtocol.*;
import Node.TransferServerHandle;
import Shared.CRC;
import Shared.Crypt;
import Shared.Defines;
import ThreadTools.ThreadControl;

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
        Crypt crypt;
        Map<Long,Long> user_destination;                            // destination for a user's packets
        long window;                                                // sets the availability to trasnmit "window" packets in a given moment
        Map<Long, RePacket> retransmission_packets;                 // packet number to packet to retransmit
        BlockingQueue<UDP_Packet> transmission_packets;             // packets to transmit
        Map<Long, BlockingQueue<TransferPacket>> received_packets;  // users to packets received
        long inc;                                                   // number of the next packet to send
        long received_number;                                       // highest number of packet received
        List<Long> non_received;                                    // list of packets with number lower than "received number" that we haven't received
        
        Connection ()
        {
            this.rwl= new ReentrantReadWriteLock();
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
        public BlockingQueue<TransferPacket> getInput (long user)
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
         * Adds a packet to the send queue. To be used by the users
         * @param user_id sending user id
         * @param transfer_packet packet to send
         * @throws Exception
         */
        public void addPacketTransmission (long user_id, TransferPacket transfer_packet) throws Exception
        {
            try
            {
                //encrypt payload
                ByteArrayOutputStream stream;
                transfer_packet.serialize(new DataOutputStream(stream= new ByteArrayOutputStream()));
                byte[] serialized= stream.toByteArray();
                byte[] encrypted_payload= this.crypt.encrypt(serialized);
                
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

        /**
         * Add a packet to the user's queue after decrypting it
         * @param packet received packet
         */
        private void receiveMessage (Message packet)
        {
            try
            {
                // decrypt the packet
                byte[] encrypted_transfer= packet.message;
                byte[] decrypted_transfer= this.crypt.decrypt(encrypted_transfer);
                ByteArrayInputStream bais = new ByteArrayInputStream(decrypted_transfer);
                DataInputStream dis = new DataInputStream(bais);
                TransferPacket transfer_packet = TransferPacket.deserialize(dis);
                //add the packet to the user's input queue
                this.received_packets.get(packet.to).add(transfer_packet);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Sends an ack packet with the given packet number
         * @param n
         */
        void ack (long n)
        {
            UDP_Packet ack_packet= new Ack(new UDP_Packet(Type.ACK, 0, 0, 0), n);
            this.transmission_packets.add(ack_packet);
        }
            
        /**
         * Method to send a single packet and add it to the retransmission queue with the sent instance defined as now.
         * @param socket socket to use
         * @param packet packet to send
         * @param now timestamp for the moment we sent the packet
         */
        private void send (DatagramSocket socket, InetAddress addr, int port, UDP_Packet packet, long now) throws Exception
        {
            //add the packet to the resend queue if it isn't an ack packet
            if (packet.type!= Type.ACK)
                this.retransmission_packets.put(packet.pNnumber, new RePacket());

            //set the serialized packet with crc-32 bitchecking 
            byte[] checked= CRC.couple(packet.serialize());
            //send the packet with crc-32 bitchecking
            socket.send(new DatagramPacket(checked, checked.length, addr, port));
        }

        /**
         * Method to send a connection's packets
         * @param socket datagram socket we are using in the socket manager
         * @param target_address target's address
         * @param target_port target port
         */
        void sendALL (DatagramSocket socket, InetAddress target_address, int target_port)
        {
            try
            {
                this.rwl.writeLock().lock();

                //get current timestamp
                long now= System.currentTimeMillis();

                //send retransmission packets
                for (RePacket repacket: this.retransmission_packets.values())
                {
                    // stop if the window is 0
                    if (this.window== 0)
                        break;

                    //Consider the packet lost if a packet is in the retransmission queue for more than "MAX_LATENCY" else we dont send it and wait for the Ack
                    if (now- repacket.added_time< MAX_LATENCY)
                    {
                        send (socket, target_address, target_port, repacket.packet, now);
                        //decrement the window to reflect the sent packet
                        this.window--;
                    }
                }

                //send other packets
                List<UDP_Packet> packets_list= new ArrayList<>();
                this.transmission_packets.drainTo(packets_list);

                for (UDP_Packet packet: packets_list)
                {
                    // stop if the window is 0
                    if (this.window== 0)
                        break;

                    send (socket, target_address, target_port, packet, now);
                    //decrement the window to reflect the sent packet
                    this.window--;
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
    ThreadControl tc;
    FileBlockInfo fbi;
    String dir;

    private static long inc= 0;                                                 
    KeyPair keys;
    Map<InetAddress, Connection> address_to_connection;
    Map<Long,Connection> user_to_connection;

    public SocketManager (ThreadControl tc)
    {
        try
        {
            this.rwl= new ReentrantReadWriteLock();
            this.keys= Crypt.generateKeyPair();
            this.address_to_connection= new HashMap<>();
            this.user_to_connection= new HashMap<>();

            //Creates the socket and starts both the sender and the receiver
            DatagramSocket socket= new DatagramSocket(Defines.transferPort);
            Thread sender= new Thread(new Sender(socket, this, tc)); 
            Thread receiver= new Thread(new Receiver(socket, this, tc));
            sender.start();
            receiver.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public UserData registerUser (InetAddress adr)
    {
        try
        {
            this.rwl.writeLock().lock();
            long user_id= inc++;

            // get user's connection
            Connection c= this.address_to_connection.get(adr);
            if (c== null)
            {
                c= new Connection();
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

    UserData registerUser (InetAddress adr, long dest)
    {
        try
        {
            this.rwl.writeLock().lock();
            long user_id= inc++;

            // get user's connection
            Connection c= this.address_to_connection.get(adr);
            if (c== null)
            {
                c= new Connection();
            }
            this.address_to_connection.put(adr, c);

            // register user in the connection
            c.registerUser(user_id, dest);
            
            // relate user to the connection
            this.user_to_connection.put(user_id, c);

            return new UserData(c, user_id);
        }
        finally
        {
            this.rwl.writeLock().unlock();
        }
    }

    /**
     * method to send every possible packet from every possible connection
     * should be used by the Sender
     * @param socket socket to send packets from
     * @param port port to send packets to
     */
    void send (DatagramSocket socket, int port)
    {
        for (Map.Entry<InetAddress, Connection> entry: this.address_to_connection.entrySet())
        {
            entry.getValue().sendALL(socket, entry.getKey(), port);
        }
    }

    //method to be used when a packet is received
    void received (DatagramPacket datagram_packet)
    {
        InetAddress from= datagram_packet.getAddress();

        // retrieve the udp packet's bytes
        byte[] payload= datagram_packet.getData();
        byte[] udp_serialized= CRC.decouple(payload);

        //Create the connection if it doesn't exist
        Connection connection= this.address_to_connection.get(from);
        if (connection== null)
        {
            connection= new Connection();
        }
        

        // if the bytes are valid
        if (udp_serialized!= null)
        {
            try
            {
                // retrieve the udp packet itself
                UDP_Packet packet= new UDP_Packet(udp_serialized);

                //set matching for the user in our side
                connection.user_destination.put(packet.to, packet.from);


                // if there is no encryption key set and the received packet is not of type connect or
                // the packet as already been received
                if ((connection.crypt== null && packet.type!= Type.CON) || (packet.pNnumber< connection.received_number&& !connection.non_received.contains(packet)))
                {
                    if (packet.pNnumber== connection.received_number)
                    {
                        // set received_number for next packet
                        connection.received_number++;
                    }
                    else
                    {
                        //if the received packet is over the expected received number
                        if (packet.pNnumber> connection.received_number)
                        {
                            // add packets in between to not received and set received number and 
                            // set received number to be the next packet to receive under normal conditions
                            while (connection.received_number!= packet.pNnumber)
                            {
                                connection.non_received.add(connection.received_number);
                                connection.received_number++;
                            } 
                            connection.received_number= packet.pNnumber+ 1;     
                        }
                        //if it's lower
                        {
                            // remove the packet from the non received list as it is now received
                            connection.non_received.remove(packet.pNnumber);
                        }
                    }

                    //Handle the packet
                    switch (packet.type)
                    {
                        case ACK:
                        {
                            // remove packet corresponding to ack from retransmission queue
                            connection.retransmission_packets.remove(packet.pNnumber);
                            //allow the window to grow
                            connection.window+= 2;
                            break;
                        }
                        case CON:
                        {
                            // handle the connection
                            Connect connect_packet= (Connect) packet;
                            // set up the crypt with the received key
                            connection.crypt= new Crypt(keys.getPrivate(), connect_packet.publicKey);
                            
                            //mark the connection as received
                            connection.ack(packet.pNnumber);
                            break;
                        }
                        case DC:
                        {
                            //not in use
                            break;
                        }
                        case MSG:
                        {
                            //ask for responder
                            if (packet.to== 0)
                            {
                                //register the user with a target
                                UserData user_data= this.registerUser(from, packet.from);
                                //change packet target to newly created user
                                packet.to= user_data.user_id;
                                //set responder to be the newly created user
                                Thread responder= new Thread(new TransferServerHandle(tc, user_data, fbi, dir));
                                responder.start();
                            }
                            connection.receiveMessage((Message) packet);
                            connection.ack(packet.pNnumber);
                            break;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
