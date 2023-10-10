public class Handle implements Runnable
{
    private FSTrackerProtocol pacote;

    Handle (FSTrackerProtocol pacote)
    {
        this.pacote= pacote;
    }

    public void run ()
    {
        switch (pacote.getType())
        {
            case 0:

        }
    }    
}
