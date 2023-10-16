package FSTracker;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadManager
{
    ReentrantLock l;
    private int t_max;
    private int t_count;

    ThreadManager ()
    {
        this.l= new ReentrantLock();
        this.t_count= 0;
        this.t_max= 4;
    }

    ThreadManager (int t_max)
    {
        this.l= new ReentrantLock();
        this.t_count= 0;
        this.t_max= t_max;
    }

    Boolean new_thread ()
    {
        if (t_max== t_count)
            return false;
        t_count++;
        return true;
    }


}
