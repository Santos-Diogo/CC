package FSTracker;

import java.util.*;

import FSProtocol.FSTrackerProtocol;

public class SolverThread implements Runnable
{
    volatile Set<FSTrackerProtocol> p_list;
    private volatile boolean stopRequested = false;

    SolverThread (Set<FSTrackerProtocol> p_list)
    {
        this.p_list= p_list;
    }

    //Synchronized method that prevents multiple calls from different threads
    private synchronized void stop ()
    {
        stopRequested= true;
    }

    //tem de ser reescrito para ter em conta que esta estrutura existe fora do scope das threads (main thread)
    private synchronized FSTrackerProtocol get_packet ()
    {
        Iterator<FSTrackerProtocol> i= p_list.iterator();
        if (i.hasNext())
        {
            FSTrackerProtocol p= i.next();
            i.remove();
            return p;
        }
        else
            return null;
    }

    public void run ()
    {
        while (!stopRequested)
        {
            FSTrackerProtocol p= get_packet();
            if (p!= null)
                Handle.handle(p);
            else
                stop();
        }
    }    
}
