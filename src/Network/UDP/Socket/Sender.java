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

import javax.swing.text.html.HTMLDocument.Iterator;

import Network.UDP.Packet.Message;
import Network.UDP.Packet.UDP_Packet;
import Network.UDP.Packet.UDP_Packet.Type;
import Network.UDP.Socket.SocketManager.ConnectionByIP;
import Network.UDP.TransferProtocol.TransferPacket;
import ThreadTools.ThreadControl;
import Shared.CRC;
import Shared.Crypt;

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

    public void udpSend (InetAddress targetAddr, UDP_Packet udp) throws Exception
    {
        //Add packet to retransmission List (With a timestamp)
        this.connections.addRetransfer(targetAddr, udp);

        //Serialize packet
        byte[] serializeUDP= udp.serialize();

        //Add checksum
        byte[] checksummed= CRC.couple(serializeUDP);

        //Make Datagram
        DatagramPacket datagramPacket= new DatagramPacket(checksummed, checksummed.length, targetAddr, Shared.Defines.transferPort);

        //Send Packet
        socket.send(datagramPacket);
    }

    /**
     * Proceedure to send out a packet
     * @param packet
     */
    public void send (InetAddress targetAddr, ConnectionByIP.Connection.Packet fatPacket) throws Exception
    {
        
        //Serialize packet
        TransferPacket packet= fatPacket.packet;
        long from= fatPacket.from;
        byte[] serializeTP= packet.serialize();
        
        //Make UDP packet
        Message udp= new Message(new UDP_Packet(Type.MSG, from, this.connections.getTargetUserId(targetAddr, from)), serializeTP);

        udpSend(targetAddr, udp);
    }

    public void run ()
    {
        try
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
                    
                    BlockingQueue<ConnectionByIP.Connection.Packet> outputQ= this.connections.getOutput(adr);
                    
                    //Need to check the retransmission Queue First
                    
                    for (ConnectionByIP.Connection.RePacket rePacket : this.connections.getRetrans(adr))
                    {
                        if ((System.currentTimeMillis()- rePacket.createTime)> 300)
                        {
                            this.connections.getRetrans(adr).remove(i);
                            udpSend (adr, rePacket.packet);
                        }
                        i++;
                    }
                    
                    
                    ConnectionByIP.Connection.Packet packet;
                    for (; i< n; i++)
                    {
                        //No more blocks to take
                        if ((packet= outputQ.peek())== null)
                        break;
                        packet= outputQ.take();
                        send(adr, packet);
                    }
                    //Decrement used packets
                    this.connections.decWindow(adr, i);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
