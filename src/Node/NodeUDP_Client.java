package Node;

import Blocker.FileBlockInfo;
import ThreadTools.ThreadControl;
import java.util.concurrent.BlockingQueue;
import TrackProtocol.*;

class NodeUDP_Client implements Runnable
{
    private FileBlockInfo fbi;                          //Info about the Node
    private BlockingQueue<UDP_Job> jobs;                //Queue of jobs to be filled by the node
    private BlockingQueue<TrackPacket> msgToTracker;    //Send a message to the server
    private ThreadControl tc;                           //Thread controll to be cascaded to minor threads

    NodeUDP_Client (FileBlockInfo fbi, BlockingQueue<UDP_Job> jobs, BlockingQueue<TrackPacket> msgToTracker, ThreadControl tc)
    {
        this.fbi= fbi;
        this.jobs= jobs;
        this.msgToTracker= msgToTracker;
        this.tc= tc;
    }    

    public void run ()
    {
        
    }
}
