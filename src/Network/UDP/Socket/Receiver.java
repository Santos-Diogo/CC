package Network.UDP.Socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import javax.xml.crypto.Data;
import java.net.SocketAddress;

import Network.UDP.Packet.*;
import Network.UDP.Socket.SocketManager.ConnectionByIP;
import Network.UDP.TransferProtocol.TransferPacket;
import Shared.CRC;
import ThreadTools.ThreadControl;

public class Receiver implements Runnable
{
    private DatagramSocket socket;
    private ConnectionByIP connections;
    private ThreadControl tc;

    Receiver (DatagramSocket socket, ConnectionByIP connections, ThreadControl tc)
    {
        this.socket= socket;
        this.connections= connections;
        this.tc= tc;
    }

    /**
     * handles connection packets received
     * @param packet packet received
     * @param from inet address from wich the packet is from
     */
    public void handleCON (Connect packet, InetAddress from)
    {
        ConnectionByIP.Connection c;

        if ((c= this.connections.getConnection (from))== null)
        {
            //create new Connection
            c= connections.new Connection(null);
        }
        //define the crypt here
    }

    public void handleACK (Ack packet, InetAddress from)
    {
        ConnectionByIP.Connection c= this.connections.getConnection(from);
        //set the packet with a given number as recieved in the other side
        c.gotPacket(packet.ackNumber);
    }

    public void handleMSG (Message packet, InetAddress from)
    {
        ConnectionByIP.Connection c= this.connections.getConnection(from);
        // handle recieving a message
    }

    public void handleDC (InetAddress from)
    {
        ConnectionByIP.Connection c= this.connections.getConnection(from);
        //Handle the dc from a given node
    }

    public void handle (UDP_Packet packet, InetAddress from)
    {
        switch (packet.type)
        {
            case CON:
                handleCON((Connect) packet, from);
                break;
            case ACK:
                handleACK((Ack) packet, from);
            case MSG:
                handleMSG((Message) packet, from);
            case DC:
                handleDC(from);
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
                    handle (udp, p.getAddress());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
