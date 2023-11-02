package ver2.ThreadTools;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class we use to controll a group of threads
 * @author Diogo Santos
 */
public class ThreadControl
{
    private static boolean end;
    private final ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock rl = l.readLock();
    private final ReentrantReadWriteLock.WriteLock wl = l.writeLock();


    public ThreadControl ()
    {
        end= false;
    }

    public void end ()
    {
        wl.lock();
        try
        {
            end= true;
        }
        finally
        {
            wl.unlock();
        }
    }

    public boolean end_check ()
    {
        rl.lock();
        try
        {
            return end;
        }
        finally
        {
            rl.unlock();
        }
    }
}