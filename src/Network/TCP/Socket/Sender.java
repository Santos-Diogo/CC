package Network.TCP.Socket;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import ThreadTools.ThreadControl;
import Network.TCP.TrackProtocol.TrackPacket;

public class Sender implements Runnable
{
    private BlockingQueue<TrackPacket> outputQueue;
    private ObjectOutputStream out;
    private ThreadControl tc;

    Sender (Socket socket, BlockingQueue<TrackPacket> outputQueue, ThreadControl tc)
    {
        try
        {
            this.outputQueue= outputQueue;
            this.out= new ObjectOutputStream(socket.getOutputStream());
            this.tc= tc;
        }
        catch (Exception e)
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
                out.writeObject(outputQueue.take());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
