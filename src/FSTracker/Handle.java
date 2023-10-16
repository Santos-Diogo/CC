package FSTracker;

import java.io.OutputStream;

import FSProtocol.FSTrackerProtocol;

public class Handle
{
    private void REG ()
    {
        
    }

    static void handle (FSTrackerProtocol pacote)
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
