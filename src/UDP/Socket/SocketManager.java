package UDP.Socket;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ThreadTools.ThreadControl;
import UDP.TransferProtocol.*;

/**
 * Class responsible for handling UDP Socket's IO and answering to some methods (?)
 */
public class SocketManager
{
    public class IOQueue
    {
        public class OutPacket
        {
            InetAddress destination;
            TransferPacket packet;
        }

        BlockingQueue<OutPacket> out;         //Packets sent
        BlockingQueue<TransferPacket> in;               //Packets recieved

        IOQueue ()
        {
            this.in= new LinkedBlockingQueue<>();
            this.out= new LinkedBlockingQueue<>();
        }
    }
    
    private ReentrantReadWriteLock rwl;
    private long nUser;
    private Map<Long, IOQueue> userToQueue;
    private Receiver receiver;
    private Sender sender;

    public SocketManager (ThreadControl tc)
    {
        try
        {
            this.rwl= new ReentrantReadWriteLock();
            this.nUser= 0;
            this.userToQueue= new HashMap<>();
            
            //Initiate Sender and Receiver
            Thread t1= new Thread(receiver= new Receiver(this, tc));
            Thread t2= new Thread(sender= new Sender(this, tc));
            t1.start();
            t2.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * creates IO chanels with a corresponding id
     * @return returns the id assotiated with the chanels
     */
    public long register ()
    {
        try
        {
            this.rwl.writeLock().lock();
            
            //Register a user creating a new q and assigning him a number
            IOQueue q= new IOQueue();
            this.sender.getSenderMinion(q.out);
            this.userToQueue.put(this.nUser, q);

            return nUser;
        }
        finally
        {
            //Increment nUser
            this.nUser+= 1;
            this.rwl.writeLock().unlock();
        }
    }

    public IOQueue getQueue (long id)
    {
        try
        {
            this.rwl.readLock().lock();
            return this.userToQueue.get(id);
        }
        finally
        {
            this.rwl.readLock().unlock();
        }
    }
}
