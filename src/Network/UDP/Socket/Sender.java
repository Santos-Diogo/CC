package Network.UDP.Socket;

import java.net.DatagramSocket;
import ThreadTools.ThreadControl;
import Shared.Defines;

public class Sender implements Runnable
{
    private SocketManager socket_manager;
    private DatagramSocket socket;
    ThreadControl tc;

    Sender (DatagramSocket socket, SocketManager socket_manager, ThreadControl tc)
    {
        this.socket= socket;
        this.socket_manager= socket_manager;
        this.tc= tc;
    }

    public void run ()
    {
        try
        {
            while (tc.get_running())
            {
                socket_manager.send(socket, Defines.transferPort);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
