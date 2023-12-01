package Node;

import java.util.concurrent.BlockingQueue;

import Blocker.FileBlockInfo;
import ThreadTools.ThreadControl;
import TrackProtocol.TrackPacket;

public class NodeUDP_ClientHandler implements Runnable
{

    private FileBlockInfo fbi;                          //Info about the Node
    private BlockingQueue<UDP_Job> jobs;                //Queue of jobs to be filled by the node
    private BlockingQueue<TrackPacket> msgToTracker;    //Send a message to the server
    private ThreadControl tc;                           //Thread controll to be cascaded to minor threads

    NodeUDP_ClientHandler (FileBlockInfo fbi, BlockingQueue<UDP_Job> jobs, BlockingQueue<TrackPacket> msgToTracker, ThreadControl tc)
    {
        this.fbi= fbi;
        this.jobs= jobs;
        this.msgToTracker= msgToTracker;
        this.tc= tc;
    }    

    public void handle (UDP_Job job)
    {

    }

    public void run ()
    {
        while (this.tc.get_running())
        {
            try
            {
                UDP_Job job= jobs.take();
                handle (job);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}
