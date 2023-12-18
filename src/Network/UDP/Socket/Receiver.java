package Network.UDP.Socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Shared.Defines;
import ThreadTools.ThreadControl;

public class Receiver implements Runnable
{
    private DatagramSocket socket;
    private SocketManager socket_manager;
    private ThreadControl tc;

    public Receiver (DatagramSocket socket, SocketManager socket_manager, ThreadControl tc)
    {
        this.socket= socket;
        this.socket_manager= socket_manager;
        this.tc= tc;      
    }

    public void run ()
    {
        try
        {
            while (this.tc.get_running())
            {
                DatagramPacket p= new DatagramPacket(new byte[Defines.transferBuffer], Defines.transferBuffer);
                socket.receive(p);
                socket_manager.received(p);                
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
