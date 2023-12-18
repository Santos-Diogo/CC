package Network.TCP.Socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import Network.TCP.TrackProtocol.TrackPacket;
import ThreadTools.ThreadControl;

public class Receiver implements Runnable
{
    private ObjectInputStream in;
    private SocketManager manager;
    private ThreadControl tc;

    Receiver (SocketManager manager, Socket s, ThreadControl tc)
    {
        try
        {
            this.in= new ObjectInputStream (s.getInputStream());
            this.manager= manager;
            this.tc= tc;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void handle (TrackPacket p)
    {
        
    }

    public void run ()
    {
        while (this.tc.get_running())
        {
            try
            {
                TrackPacket p= (TrackPacket) in.readObject();
                handle (p);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

