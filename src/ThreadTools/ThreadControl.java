package ThreadTools;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class we use to controll a group of threads
 */
public class ThreadControl
{
    private static boolean running;
    private final ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock rl = l.readLock();
    private final ReentrantReadWriteLock.WriteLock wl = l.writeLock();


    public ThreadControl ()
    {
        running= true;
    }

    public void set_running (boolean b)
    {
        wl.lock();
        try
        {
            running= b;
        }
        finally
        {
            wl.unlock();
        }
    }

    public boolean get_running ()
    {
        rl.lock();
        try
        {
            return running;
        }
        finally
        {
            rl.unlock();
        }
    }
}