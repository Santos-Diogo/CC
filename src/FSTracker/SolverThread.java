package FSTracker;

import java.util.*;

import FSProtocol.FSTrackerProtocol;

/**
 * Class that defines the SolverThreads.<p>
 * This are threads created by the MainThread that actually handle the packets collected by the MainThread and put into the PacketManager
 * @author Diogo Santos
 */
public class SolverThread implements Runnable
{
    PacketManager pm;
    private volatile boolean stopRequested = false;

    SolverThread (PacketManager pm)
    {
        this.pm= pm;
    }

    //Synchronized method that prevents multiple calls from different threads
    private synchronized void stop ()
    {
        stopRequested= true;
    }

    public void run ()
    {
        while (!stopRequested)
        {
            FSTrackerProtocol p= pm.get_packet();
            if (p!= null)
                Handle.handle(p);
            else
                stop();
        }
    }    
}
