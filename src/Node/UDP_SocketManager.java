package Node;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

import ThreadTools.*;
import UDP.*;

/**
 * Class responsible for handling UDP Socket's IO and answering to some methods (?)
 */
class UDP_SocketManager
{
    /**
     * Class that combines a Q of packets to transmit to a given Node with the know information on that Connection
     */
    private class UDP_Out
    {
        BlockingQueue<DatagramPacket> outPackets;
        ConnectionInfo connectionInfo;

        UDP_Out (BlockingQueue<DatagramPacket> outPackets)
        {
            this.outPackets= outPackets;
            this.connectionInfo= new ConnectionInfo();
        }

        void add_packet (DatagramPacket p)
        {
            outPackets.add(p);
        }
    }

    private DatagramSocket socket;                                              //UDP Socket to use
    private BlockingQueue<DatagramPacket> inputPacketServer;                    //Packets recieved to give to server
    private BlockingQueue<DatagramPacket> inputPacketClient;                    //Packets recieved to give to client
    private Map<InetAddress, UDP_Out> ipToOutput;                               //Maps a node to packets to transmit and connection info
    private Thread sender;                                                      //Thread to send nodes
    private Thread reciever;                                                    //Thread to recieve nodes
    private ThreadControl tc;                                                   //Thread controller to be passed down to workers
    
    UDP_SocketManager (DatagramSocket s, BlockingQueue<DatagramPacket> inputServer, BlockingQueue<DatagramPacket> inputClient, ThreadControl tc)
    {
        this.socket= s;
        this.inputPacketServer= inputServer;
        this.inputPacketClient= inputClient;
        this.ipToOutput= new HashMap<>();

        //Start recieving and sending minions
        this.sender= new Thread();
        sender.start();
        this.reciever= new Thread();
        reciever.start();

        
        this.tc= tc;
    }

    /**
     * Send a packet to a given Adress
     * @param p packet to send
     * @param target target node
     */
    void send (DatagramPacket p, InetAddress target)
    {
        BlockingQueue<DatagramPacket> q;

        // if there is already a map entry to that node
        if (this.ipToOutput.containsKey(target))
        {
            q= this.ipToOutput.get(target).outPackets;
        }
        // if this is the first connection to the specific node
        else
        {
            q= new LinkedBlockingQueue<>();
            this.ipToOutput.put(target, new UDP_Out(q));
        }

        // Add the packet to the corresponding send Q
        q.add(p);
    }
}
