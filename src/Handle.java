import java.io.OutputStream;

public class Handle implements Runnable
{
    private FSTrackerProtocol pacote;

    Handle (FSTrackerProtocol pacote)
    {
        this.pacote= pacote;
    }

    private void REG ()
    {
        //S
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
