package Node;

import java.io.IOException;
import java.net.DatagramSocket;

import ThreadTools.ThreadControl;

/**
 * Class responsible for handling incoming connections from other nodes (transfers)
 */
public class NodeHost implements Runnable
{
    DatagramSocket hub;
    ThreadControl tc;

    public NodeHost (ThreadControl tc)
    {
        this.tc= tc;
        try
        {
            this.hub= new DatagramSocket(NodeDefines.hubPort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void run ()
    {
        while (tc.get_running())
        {
            try
            {
                hub.receive(null);
                // Instanciar recetores
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
