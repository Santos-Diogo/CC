package Node;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.net.DatagramPacket;
import java.util.Map;

import Shared.NetId;
import ThreadTools.ThreadControl;

/**
 * Class responsible for handling UDP Socket's IO and answering to some methods (?)
 */
class UDP_SocketManager implements Runnable
{
    class OutputPackets
    {
        class Output 
        {
            private BlockingQueue<DatagramPacket> outputPackets;            //Blocking Queue filled by Output creating Node
            private long windowSize;
            
            Output (BlockingQueue<DatagramPacket> b)
            {
                this.outputPackets= b;
                this.windowSize= 1;
            }
        }

        private ReentrantReadWriteLock rwl;
        private Map<NetId, Output> idToPackets;
    }
    
    class Input
    {
        private BlockingQueue<DatagramPacket> inputPackets;             //Blocking Queue consumed by Input creating Node

        Input (BlockingQueue<DatagramPacket> b) {this.inputPackets=b;}
    }

    private DatagramSocket socket;                                      //UDP Socket to use
    private Map<NetId, Input> inputPackets;                             //Packets recieved
    private OutputPackets outputPackets;                                //Packets to be transmited
    private ThreadControl tc;                                           //Thread controller to be passed down to workers



    UDP_SocketManager (DatagramSocket s)
    {
        this.socket= s;
    }

    public void run ()
    {
        while (this.tc.get_running())
        {
            
        }
    }
}
