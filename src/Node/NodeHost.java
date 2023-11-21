package Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import Shared.*;

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
            this.hub= new DatagramSocket(Shared.Defines.nodeHostPort);
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
                //Create a new packet
                byte[] buf= new byte[Defines.transferBuffer];
                DatagramPacket p= new DatagramPacket(buf, 0);

                //Recieve packet
                hub.receive(p);

                // Instanciar handlers
                Thread t= new Thread(new NodeHostMinion (p));
                t.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
