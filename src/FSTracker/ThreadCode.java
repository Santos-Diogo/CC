package FSTracker;

import java.io.OutputStream;

import FSProtocol.FSTrackerProtocol;

public class ThreadCode implements Runnable
{
    private FSTrackerProtocol pacote;

    ThreadCode (FSTrackerProtocol pacote)
    {
        this.pacote= pacote;
    }

    public void run ()
    {
        Handle.handle(pacote);
    }    
}
