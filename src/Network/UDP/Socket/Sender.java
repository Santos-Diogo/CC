package Network.UDP.Socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Network.UDP.Socket.SocketManager.ConnectionByIP;
import Network.UDP.TransferProtocol.TransferPacket;
import ThreadTools.ThreadControl;

public class Sender implements Runnable
{
    private DatagramSocket socket;
    ConnectionByIP connection;
    ThreadControl tc;

    Sender (DatagramSocket socket, ConnectionByIP connection, ThreadControl tc)
    {
        this.socket= socket;
        this.connection= connection;
        this.tc= tc;
    }

    public void run ()
    {
        while (tc.get_running())
        {
            try
            {
                //Iterate over the Connections and send all possible packets while checking for acks
                
            }
            finally
            {

            }
        }
    }
}
