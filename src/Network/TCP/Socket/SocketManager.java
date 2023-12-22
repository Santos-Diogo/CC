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
    private Socket socket;
    private ThreadControl tc;
    private Thread t1;
    private Thread t2;

    public SocketManager (Socket socket, ThreadControl tc)
    {
        try
        {
            this.rwl= new ReentrantReadWriteLock();
            this.nUser= 0;
            this.userToInput= new HashMap<>();
            this.outputQueue = new LinkedBlockingQueue<>();
            this.tc = tc;
            //Initiate Sender and Receiver
            
            t2= new Thread(new Sender(socket, outputQueue, tc));
            t1= new Thread(new Receiver(this, socket, tc));
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
            
            
            return nUser;
        }
        finally
        {
            //Increment nUser
            this.nUser+= 1;
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

    public void stop ()
    {
        //stop threads
        this.tc.set_running(false);

        try
        {
            // wait for threads
            this.t1.interrupt();
            this.t1.join();
            this.t2.interrupt();
            this.t2.join();

            // close socket
            this.socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
            
    }
}
