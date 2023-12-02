package Node;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.security.KeyPair;

import TransferProtocol.*;
import ThreadTools.ThreadControl;

class TransferClientHandle implements Runnable
{
    private InetAddress target;                                             //Node to send packets to
    private BlockingQueue<TransferJob> jobs;                                //Job Input
    private BlockingQueue<TransferPacket> input;                            //Input from socket
    private BlockingQueue<DatagramPacket> output;                           //Output from socket
    private ThreadControl tc;                                               //Thread Controll
    private KeyPair keyPair;                                                //Own Security KeyPair

    TransferClientHandle (  InetAddress target,
                            BlockingQueue<TransferJob> jobs,
                            BlockingQueue<TransferPacket> input,
                            BlockingQueue<DatagramPacket> output,
                            ThreadControl tc,
                            KeyPair keyPair)
    {
        this.target= target;
        this.jobs= jobs;
        this.input= input;
        this.output= output;
        this.tc= tc;
        this.keyPair= keyPair;
    }

    private void connect ()
    {
        
    }

    private void handle (TransferJob j)
    {

    }

    public void run ()
    {
        //Connect with target node
        connect ();

        while (this.tc.get_running())
        {
            try
            {
                handle(jobs.take());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        //Send disconnect message?
    }
}
