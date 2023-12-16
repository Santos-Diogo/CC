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
        public class ConnectionStatus
        {
            //undefined requisites
            ConnectionStatus ()
            {

            }
        }

        ReentrantReadWriteLock rwl;
        KeyPair keys;
        Crypt crypt;
        Map<Long,Long> user_destination;                            // destination for a user's packets
        Map<Long, BlockingQueue<RePacket>> retransmission_packets;  // packet's timestamp to packet to retransmit
        BlockingQueue<UDP_Packet> transmission_packets;             // packets to transmit
        Map<Long, BlockingQueue<TransferPacket>> received_packets;  // users to packets received
        ConnectionStatus status;                                    // statistics on the connection/ connection control
        long received_number;                                       // highest number of packet received
        List<Long> non_received;                                    // list of packets with number lower than "received number" that we haven't received

        Connection (KeyPair keys)
        {
            this.rwl= new ReentrantReadWriteLock();
            this.keys= keys;
            this.crypt= null;
            this.user_destination= new HashMap<>();
            this.transmission_packets= new LinkedBlockingQueue<>();
            this.retransmission_packets= new HashMap<>();
            this.status= new ConnectionStatus();
            this.received_number= 0;
            this.non_received= new ArrayList<>();

            //add a connection message to the sender;
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
            return this.received_packets.get(user);
        }

        /**
         * Adds a packet to the send queue
         */
        void addPacket ()
        {
            
        }

        /**
         * Method to be called by the sender to send every possible packet in accordance with the connection's own state
         */
        void send ()
        {

        }
    }

    ReentrantReadWriteLock rwl;
    KeyPair keys;
    Map<InetAddress, Connection> address_to_connection;
    Map<Long,Connection> user_to_connection;

    SocketManager ()
    {
        this.rwl= new ReentrantReadWriteLock();
        // generate the keypair
        // this.keys= ;
    }
}
