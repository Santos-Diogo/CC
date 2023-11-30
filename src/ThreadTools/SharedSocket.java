package ThreadTools;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Thread that handles socket IO operations for multiple Threads
 */
public class SharedSocket
{
    private final DatagramSocket socket;
    
    public SharedSocket (DatagramSocket socket)
    {
        this.socket= socket;
    }

    public synchronized void receive (DatagramPacket packet) throws Exception
    {
        this.socket.receive(packet);
    }

    public synchronized void send (DatagramPacket packet) throws Exception
    {
        this.socket.send(packet);
    }

    public void close ()
    {
        this.socket.close();
    }
}
