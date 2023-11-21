package Node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NodeSender
{
    private DatagramSocket socket;

    public NodeSender (DatagramSocket socket)
    {
        this.socket= socket;
    }

    public void send (String msg)
    {
        byte[] buf= msg.getBytes();
        
        try
        {
            socket.send(new DatagramPacket(buf, buf.length));
            System.out.println("Mensagem UDP enviada");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
