package Network.TCP.Socket;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Network.TCP.TrackProtocol.TrackPacket;
import ThreadTools.ThreadControl;

public class SocketManager 
{   
    private ReentrantReadWriteLock rwl;
    private long nUser;
    private Map<Long, BlockingQueue<TrackPacket>> userToInput;      // Match user with corresponding input Queue
    private BlockingQueue<TrackPacket> outputQueue;                 // We only need one out

    public SocketManager (Socket socket, ThreadControl tc)
    {
        try
        {
            this.rwl= new ReentrantReadWriteLock();
            this.nUser= 0;
            this.userToInput= new HashMap<>();
            
            //Initiate Sender and Receiver
            Thread t1= new Thread(new Receiver(this, socket, tc));
            Thread t2= new Thread(new Sender(socket, outputQueue, tc));
            t1.start();
            t2.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * creates Input chanels with a corresponding id
     * @return returns the id assotiated with the chanels
     */
    public long register ()
    {
        try
        {
            this.rwl.writeLock().lock();
            
            //Register a user creating a new input q and assigning him a number
            BlockingQueue<TrackPacket> q= new LinkedBlockingQueue<>();
            this.userToInput.put(this.nUser, q);

            //Increment nUser
            this.nUser+= 1;
            
            return nUser;
        }
        finally
        {
            this.rwl.writeLock().unlock();
        }
    }

    /**
     * @return the shared output queue
     */
    public BlockingQueue<TrackPacket> getOutpuQueue () 
    {
        return this.outputQueue;
    }

    /**
     * @param id
     * @return input user's input queue
     */
    public BlockingQueue<TrackPacket> getInputQueue (long id)
    {
        try
        {
            this.rwl.readLock().lock();
            return this.userToInput.get(id);
        }
        finally
        {
            this.rwl.readLock().unlock();
        }
    }
}
