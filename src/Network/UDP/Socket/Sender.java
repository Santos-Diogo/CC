package Network.UDP.Socket;

import java.util.List;
import java.util.Set;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Network.UDP.Packet.Message;
import Network.UDP.Packet.UDP_Packet;
import Network.UDP.Socket.SocketManager.ConnectionByIP;
import Network.UDP.TransferProtocol.TransferPacket;
import ThreadTools.ThreadControl;

public class Sender implements Runnable
{
    private DatagramSocket socket;
    ConnectionByIP connections;
    ThreadControl tc;

    Sender (DatagramSocket socket, ConnectionByIP connections, ThreadControl tc)
    {
        this.socket= socket;
        this.connections= connections;
        this.tc= tc;
    }

    /**
     * Proceedure to send out a packet
     * @param packet
     */
    public void send (TransferPacket packet) throws Exception
    {
        //Serialize packet
        byte[] serializesTP= packet.serialize();

        //Make UDP packet
        UDP_Packet udp= new Message(from, to, serializesTP);

        //Serialize packet
        
        //Encrypt packet with Connection Key
        
        //Make Datagram
        
        //Add packet to retransmission List (With a timestamp)
        
        //Send Packet
    }

    public void run ()
    {
        while (tc.get_running())
        {
            //Get all targets
            Set<InetAddress> targetsIP= connections.getTargetsIp ();

            //Iterate over the Connections
            ConnectionByIP.Connection c;
            for (InetAddress adr : targetsIP)
            {
                int n= this.connections.getWindow(adr);
                int i= 0;

                BlockingQueue<TransferPacket> outputQ= this.connections.getOutput(adr);

                //Need to check the retransmission Queue First

                TransferPacket packet;
                for (; i< n; i++)
                {
                    //No more blocks to take
                    if ((packet= outputQ.peek())== null)
                        break;
                    
                    try
                    {
                        packet= outputQ.take();
                        send(packet);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                
                //Decrement used packets
                this.connections.decWindow(adr, i);
            }

        }
    }
}
