package FSTracker;

import FSProtocol.FSTrackerProtocol;

public class Handle
{
    private static void REG ()
    {
        
    }

    static void handle (FSTrackerProtocol pacote)
    {
        switch (pacote.getTypeMsg())
        {
            case REG:
                REG ();
                break;
            default:
                System.out.println("Unknowkn type in the packet");
        }
    }    
}
