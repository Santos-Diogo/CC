package Network.UDP.Socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.PublicKey;
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
    /**
     * Used for Output
     * Maps IP adresses with the manager's connection and connection data with the node with that Address
     */
    public class ConnectionByIP
    {
        /**
         * Connection to other node
         */
        public class Connection
        {
            /* public class Packet
            {
                public long from;
                public TransferPacket packet;

                Packet (long from, TransferPacket packet)
                {
                    this.from= from;
                    this.packet= packet;
                }
            } */
            public class RePacket
            {
                public long createTime;                     //milisecond the packet was created at
                public UDP_Packet packet;                   //packet to retransmit

                RePacket (UDP_Packet p)
                {
                    this.createTime= System.currentTimeMillis();
                    this.packet= p;
                }
            }

            ReentrantReadWriteLock rwl;     
            long packetNum;                                 //number of the packet. used for ACK and retransmission handling
            int window;                                     //determines the availability of packets to be sent
            Map<Long, Long> userConnectedTo;                //matches a user with the other side's message receiver
            KeyPair keypair;                                //own keypair
            Crypt crypt;                                    //used for cryptography. set up in connection
            Map<Long, RePacket> packetsToRetrans;           //list of packets to be retransmited if time elapses
            BlockingQueue<UDP_Packet> packetsToTransfer;    //output for the nodes transmiting to a given IP/user combination (we allways use the defined port)
            //Other Parameters
            
            /**
             * Creates a connection adding a connection packet to the send Queue
             * @param keyPair keypair to be used when connecting to the other node
             */
            public Connection (KeyPair keyPair)
            {
                this.rwl= new ReentrantReadWriteLock();
                this.window= 1;
                this.userConnectedTo= new HashMap<>();
                this.crypt= null;
                this.packetsToTransfer= new LinkedBlockingQueue<>();

                //Creates a packet to connect to the target node
                this.addPacket(new Connect(new UDP_Packet(Type.CON, 0, 0), keyPair.getPublic()));
            }

            public void addRetransfer (UDP_Packet p)
            {
                try
                {
                    this.rwl.writeLock().lock();
                    RePacket reP= new RePacket(p);
                    this.packetsToRetrans.put(p.pNnumber, reP);
                }
                finally
                {
                    this.rwl.writeLock().unlock();
                }
            }
            
            public void addUser (long user)
            {
                try
                {
                    this.rwl.writeLock().lock();
                    this.userConnectedTo.put(user, (long)0);                  //Set 0 as default to send to the server
                }
                finally
                {
                    this.rwl.writeLock().unlock();
                }
            }
            
            public void setConnected (long user, long target)
            {
                try
                {
                    this.rwl.writeLock().lock();
                    this.userConnectedTo.put(user, target);
                }
                finally
                {
                    this.rwl.writeLock().unlock();
                }
            }
            
            public long getUserConnectedTo (long user)
            {
                try
                {
                    this.rwl.readLock().unlock();
                    return this.userConnectedTo.get(user);
                }
                finally
                {
                    this.rwl.readLock().unlock();
                }
            }

            public void incWindow (int value)
            {
                try
                {
                    this.rwl.writeLock().lock();
                    this.window+= value;

                }
                finally
                {
                    this.rwl.writeLock().unlock();;
                }
            }

            public void decWindow (int value)
            {
                try
                {
                    this.rwl.writeLock().lock();
                    if (this.window-value> 1)
                        this.window= value;
                }
                finally
                {
                    this.rwl.writeLock().unlock();
                }
            }

            public int getWindow ()
            {
                try
                {
                    this.rwl.readLock().lock();
                    return this.window;
                }
                finally
                {
                    this.rwl.readLock().unlock();
                }
            }

            List<RePacket> getRetrans ()
            {
                return this.getRetrans();
            }


            /**
             * adds a packet to the send queue
             * @param packet
             */
            public void addPacket (Packet packet, UDP_Packet.Type type)
            {
                try
                {
                    //get read lock
                    this.rwl.readLock().lock();

                    UDP_Packet udp;
                    udp= new UDP_Packet(type, packet.from, this.userConnectedTo.get(packet.from));
                    
                    // Create packet according to type and stuff
                    switch (type)
                    {
                        case CON:
                        {
                            udp= new Connect(udp, this.keypair.getPublic());
                            break;
                        }
                        case DC:
                        {
                            // TODO
                            break;
                        }
                        case MSG:
                        {
                            udp= new Message(udp, packet.packet.serialize());
                            break;
                        }
                        case ACK:
                        {
                            // TODO
                            break;
                        }
                    }
                    
                    //get write lock
                    this.rwl.writeLock().lock();

                    //Add packet to transmission queue
                    this.packetsToTransfer.add(udp);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    this.rwl.writeLock().unlock();
                    this.rwl.readLock().unlock();
                }
            }
        }

        private ReentrantReadWriteLock rwl;
        private Map <InetAddress, Connection> map;                      //group connections by adress
        private Map<Long, InetAddress> idToConnection;                  //match a user with it's target adress
        private Map<Long, BlockingQueue<TransferPacket>> input;         //match user with it's input
        private KeyPair keyPair;                                        //key pair used for encryption

        ConnectionByIP ()
        {
            this.rwl= new ReentrantReadWriteLock();
            this.map= new HashMap<>();
        }

        UserIO addUser (long user, InetAddress target)
        {
            try
            {
                this.rwl.readLock().lock();
                ConnectionByIP.Connection c;
                //Check if the connection exists
                if (this.map.containsKey(target))
                {
                    c= this.map.get(target);
                }
                else
                {
                    c= new Connection(this.keyPair);
                }
                
                //Add user to connection
                c.addUser(user);
                
                //Set up user-connection matcher
                this.idToConnection.put(user, target);
                
                //Add userInput
                LinkedBlockingQueue<TransferPacket> userInput= new LinkedBlockingQueue<>();
                this.input.put(user, userInput);
                
                //Return the user's Input and Connection to interact with
                return new UserIO(userInput);
            }
            finally
            {
                this.rwl.writeLock().unlock();
            }
        }

        /**
         * @return Returns server's IO channels
         */
        UserIO addServer ()
        {
            try
            {
                this.rwl.readLock().lock();
                LinkedBlockingQueue<TransferPacket> serverInput= new LinkedBlockingQueue<>();
                this.input.put((long) 0, serverInput);
                return new UserIO(serverInput, null);
            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }

        Set<InetAddress> getTargetsIp ()
        {
            try
            {
                this.rwl.writeLock().lock();
                return this.map.keySet();
            }
            finally
            {
                this.rwl.writeLock().unlock();
            }
        }

        public void incWindow (InetAddress adr, int value)
        {
            try
            {
                this.rwl.readLock().lock();
                Connection c= this.map.get(adr);
                c.incWindow(value);
            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }

        public void decWindow (InetAddress adr, int value)
        {
            try
            {
                this.rwl.readLock().lock();
                Connection c= this.map.get(adr);
                c.decWindow(value);
            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }

        public int getWindow (InetAddress adr)
        {
            try
            {
                this.rwl.readLock().lock();
                Connection c= this.map.get(adr);
                return c.getWindow();
            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }

        public long getTargetUserId (InetAddress addr, long from)
        {
            try
            {
                this.rwl.readLock().lock();
                return this.map.get(addr).getUserConnectedTo(from);
            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }

        public Crypt getCrypt (InetAddress addr)
        {
            return this.map.get(addr).crypt;
        }

        public void addRetransfer (InetAddress addr, UDP_Packet p)
        {
            try
            {
                this.rwl.readLock().lock();
                this.map.get(addr).addRetransfer(p);
            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }
        
        public List<ConnectionByIP.Connection.RePacket> getRetrans (InetAddress addr)
        {
            return this.map.get(addr).getRetrans();
        }
        
        public BlockingQueue<TransferPacket> getUserInput (long userId)
        {
            return this.input.get(userId);
        }

        public ConnectionByIP.Connection getConnection (InetAddress addr)
        {
            return this.map.get(addr);
        }
    }

    public class UserIO
    {
        public BlockingQueue<TransferPacket> in;
        public BlockingQueue<ConnectionByIP.Connection.Packet> out;
        
        UserIO (BlockingQueue<TransferPacket> in, BlockingQueue<ConnectionByIP.Connection.Packet> out)
        {
            this.in= in;
            this.out= out;
        }

        UserIO (UserIO io)
        {
            this.in= io.in;
            this.out= io.out;
        }
    }

    public class UserInfo extends UserIO
    {  
        public long id;
        
        UserInfo (long id, BlockingQueue<TransferPacket> in, BlockingQueue<ConnectionByIP.Connection.Packet> out)
        {
            super (in, out);
            this.id= id;
        }

        UserInfo (long id, UserIO io)
        {
            super (io);
            this.id= id;
        }
    }
    
    private ReentrantReadWriteLock rwl;
    private long nUser;
    private ConnectionByIP connectivity;
    private Receiver receiver;
    private Sender sender;

    public SocketManager (ThreadControl tc)
    {
        try
        {
            this.rwl= new ReentrantReadWriteLock();
            this.nUser= 1;
            this.connectivity= new ConnectionByIP();
            
            //Set up the socket
            DatagramSocket socket= new DatagramSocket(Shared.Defines.transferPort);

            //Initiate Sender and Receiver
            Thread t1= new Thread(receiver= new Receiver(socket, this.connectivity, tc));
            Thread t2= new Thread(sender= new Sender(socket, this.connectivity, tc));
            t1.start();
            t2.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public UserInfo register (InetAddress target)
    {
        //register a user associated with the adress' output connection and giving him an input connection
        try
        {
            this.rwl.writeLock().lock();
            
            //Get the user's id
            long id= this.nUser;
            
            //Get user IO and send it with the user's assigned id
            return new UserInfo(id, this.connectivity.addUser(id, target));
        }
        finally
        {
            this.nUser+= 1;
            this.rwl.writeLock().unlock();
        }
    }

    public UserInfo registerServer ()
    {
        try
        {
            this.rwl.writeLock().lock();
            //Set Input
            

            return new UserInfo(0, this.connectivity.addServer());
        }
        finally
        {
            this.rwl.writeLock().unlock();
        }
    }

}
