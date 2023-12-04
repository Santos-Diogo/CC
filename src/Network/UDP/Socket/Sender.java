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

    /**
     * Proceedure to send out a packet
     * @param packet
     */
    public void send (InetAddress targetAddr, ConnectionByIP.Connection.Packet fatPacket, Crypt crypt) throws Exception
    {
        //Add packet to retransmission List (With a timestamp)
        

        //Serialize packet
        TransferPacket packet= fatPacket.packet;
        long from= fatPacket.from;
        byte[] serializeTP= packet.serialize();

        //Make UDP packet
        Message udp= new Message(from, this.connections.getTargetUserId(targetAddr, from), serializeTP);

        //Serialize packet
        byte[] serializeUDP= udp.serialize();

        //Encrypt packet with Connection Key
        byte[] encrypted= crypt.encrypt(serializeUDP);

        //Make Datagram
        DatagramPacket datagramPacket= new DatagramPacket(encrypted, encrypted.length, targetAddr, Shared.Defines.transferPort);
        

        //@TODO

        //Send Packet
        socket.send(datagramPacket);
    }

    //@TODO!
    Crypt connect (InetAddress addr)
    {
        return null;
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

                BlockingQueue<ConnectionByIP.Connection.Packet> outputQ= this.connections.getOutput(adr);

                //Need to check the retransmission Queue First

                //Check if crypt is null and if so, connect
                Crypt crypt;
                if ((crypt= this.connections.getCrypt(adr))== null)
                {
                    //Create and set crypt
                    crypt= connect (adr);

                }

                ConnectionByIP.Connection.Packet packet;
                for (; i< n; i++)
                {
                    //No more blocks to take
                    if ((packet= outputQ.peek())== null)
                        break;
                    
                    try
                    {
                        packet= outputQ.take();
                        send(adr, packet, crypt);
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
