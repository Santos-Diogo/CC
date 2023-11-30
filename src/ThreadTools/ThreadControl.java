package ThreadTools;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class we use to controll a group of threads
 */
public class ThreadControl
{
    private static boolean running;
    private final ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    
    public ThreadControl ()
    {
        running= true;
    }

    public void set_running (boolean b)
    {
        l.writeLock().lock();
        try
        {
            running= b;
        }
        finally
        {
            l.writeLock().unlock();
        }
    }

    public boolean get_running ()
    {
        l.readLock().lock();
        try
        {
            return running;
        }
        finally
        {
            l.readLock().unlock();
        }
    }
}