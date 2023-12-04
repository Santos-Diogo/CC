package Network.UDP.Socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.xml.crypto.Data;

import Network.UDP.Socket.SocketManager.ConnectionByIP;
import Network.UDP.TransferProtocol.TransferPacket;
import Shared.CRC;
import Shared.Crypt;
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

    public void receive (InetAddress addr, Crypt crypt)
    {
        DatagramPacket p;
        socket.receive(p);
        crypt.decrypt(p)
    }

    public void run ()
    {
        
    }
}
