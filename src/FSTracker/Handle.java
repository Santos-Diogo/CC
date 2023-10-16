package FSTracker;
import java.io.OutputStream;

import FSProtocol.FSTrackerProtocol;

public class Handle implements Runnable
{
    private FSTrackerProtocol pacote;

    Handle (FSTrackerProtocol pacote)
    {
        this.pacote= pacote;
    }

    private void REG ()
    {
        
    }

    public void run ()
    {
        switch (pacote.getType())
        {
            case 0:
                REG ();
                break;
            default:
                System.out.println("Unknowkn type in the packet");
        }
    }    
}
