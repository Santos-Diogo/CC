package UDP.Socket;

import java.net.InetAddress;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

import ThreadTools.*;
import UDP.TransferProtocol.TransferPacket;

/**
 * Class responsible for handling UDP Socket's IO and answering to some methods (?)
 */
class SocketManager
{
    /**
     * Class that combines a Q of packets to transmit to a given Node with the know information on that Connection
     */
    class UDP_Out
    {
        BlockingQueue<DatagramPacket> outPackets;
        ConnectionInfo connectionInfo;

        UDP_Out (BlockingQueue<DatagramPacket> outPackets)
        {
            this.outPackets= outPackets;
            this.connectionInfo= new ConnectionInfo(1);                         //
        }

        void add_packet (DatagramPacket p)
        {
            outPackets.add(p);
        }
    }

    private DatagramSocket socket;                                              //UDP Socket to use
    private Map<InetAddress, UDP_Out> ipToOutput;                               //Maps a node to packets to transmit and connection info
    private Thread sender;                                                      //Thread to send nodes
    private Thread reciever;                                                    //Thread to recieve nodes
    private ThreadControl tc;                                                   //Thread controller to be passed down to workers
    
    /**
     * 
     * @param s UDP Socket to use
     * @param inputServer Q where to write Server's input
     * @param inputClient Q where to write Client's input
     * @param tc Thread Controll
     */
    SocketManager (DatagramSocket s, BlockingQueue<TransferPacket> inputServer, BlockingQueue<TransferPacket> inputClient, ThreadControl tc)
    {
        this.socket= s;
        this.ipToOutput= new HashMap<>();

        //Start recieving and sending minions
        this.sender= new Thread(new Sender(socket, ipToOutput, tc));
        sender.start();
        this.reciever= new Thread(new Receiver(socket, inputServer, inputClient, tc));
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
