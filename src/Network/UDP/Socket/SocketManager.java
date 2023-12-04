package Network.UDP.Socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ThreadTools.ThreadControl;
import Network.UDP.TransferProtocol.*;

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
            ReentrantReadWriteLock rwl;
            int window;
            Map<Long, Long> userConnectedTo;
            BlockingQueue<TransferPacket> packetsToTransfer;        //output for the nodes transmiting to a given IP 
            //Other Parameters
            
            public Connection ()
            {
                this.rwl= new ReentrantReadWriteLock();
                this.window= 1;
                this.userConnectedTo= new HashMap<>();
                this.packetsToTransfer= new LinkedBlockingQueue<>();   
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
            
            public BlockingQueue<TransferPacket> getOutput ()
            {
                return this.packetsToTransfer;
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
        }

        private ReentrantReadWriteLock rwl;
        private Map <InetAddress, Connection> map;                      //group connections by adress
        private Map<Long, InetAddress> idToConnection;                  //match a user with it's target adress
        private Map<Long, BlockingQueue<TransferPacket>> input;         //match user with it's input

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
                    c= new Connection();
                }
                
                //Add user to connection
                c.addUser(user);
                
                //Set up user-connection matcher
                this.idToConnection.put(user, target);
                
                //Add userInput
                LinkedBlockingQueue<TransferPacket> userInput= new LinkedBlockingQueue<>();
                this.input.put(user, userInput);
                
                //Return the user's connections
                return new UserIO(userInput, c.getOutput());
            }
            finally
            {
                this.rwl.writeLock().unlock();
            }
        }

        UserIO addServer ()
        {
            try
            {

            }
        }
    }

    public class UserIO
    {
        public BlockingQueue<TransferPacket> in;
        public BlockingQueue<TransferPacket> out;

        UserIO (BlockingQueue<TransferPacket> in, BlockingQueue<TransferPacket> out)
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

        UserInfo (long id, BlockingQueue<TransferPacket> in, BlockingQueue<TransferPacket> out)
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
            
            //Set up the socket
            DatagramSocket socket= new DatagramSocket(Shared.Defines.transferPort);

            //Initiate Sender and Receiver
            Thread t1= new Thread(receiver= new Receiver(socket, this, tc));
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
            

            return new UserInfo(0, this.connectivity.addServer(), null);
        }
        finally
        {
            this.rwl.writeLock().unlock();
        }
    }
}
