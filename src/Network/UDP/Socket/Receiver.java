package Network.UDP.Socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

import javax.xml.crypto.Data;

import Network.UDP.Packet.UDP_Packet;
import Network.UDP.Socket.SocketManager.ConnectionByIP;
import Network.UDP.TransferProtocol.TransferPacket;
import Shared.CRC;
import ThreadTools.ThreadControl;

public class Receiver implements Runnable
{
    private DatagramSocket socket;
    private ConnectionByIP connection;
    private ThreadControl tc;

    Receiver (DatagramSocket socket, ConnectionByIP connection, ThreadControl tc)
    {
        this.socket= socket;
        this.connection= connection;
        this.tc= tc;
    }

    public void handleCon (UDP_Packet packet)
    {
        packet.
    }

    public void handle (UDP_Packet packet)
    {
        switch (packet.type)
        {
            case CON:
                handleCon ()
            case ACK:

            case MSG:

            case DC:

        }
    }

    public void run ()
    {
        try
        {
            while (this.tc.get_running())
            {
                DatagramPacket p= new DatagramPacket (new byte[Shared.Defines.transferBuffer], Shared.Defines.transferBuffer);
                socket.receive(p);
                byte[] crude;
                
                // if the check failed we drop the packet
                if ((crude= CRC.decouple(p.getData()))!= null)
                {
                    UDP_Packet udp= new UDP_Packet(crude);
                    handle (udp);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
