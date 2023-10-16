import java.util.concurrent.locks.ReentrantLock;

public class ThreadManager
{
    ReentrantLock l;
    private int t_max;
    private int t_count;

    ThreadManager ()
    {
        l= new ReentrantLock();
        t_max= 4;
        t_count= 0;
    }

    Boolean new_thread ()
    {
        if (t_max== t_count)
            return false;
        t_count++;
        return true;
    }

    
}
