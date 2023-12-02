package Node;

import java.util.concurrent.LinkedBlockingQueue;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.KeyPair;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import ThreadTools.ThreadControl;
import TransferProtocol.TransferPacket;

public class TransferClient implements Runnable
{
    class HandlerData
    {
        BlockingQueue<TransferJob> inJobs;                          //Pass jobs down to Handlers
        BlockingQueue<TransferPacket> inPacket;                     //Pass packets down to Handlers
        BlockingQueue<DatagramPacket> outPacket;                    //Pass packets to transfer to the target via shared socket

        HandlerData (BlockingQueue<TransferJob> handlerJ, BlockingQueue<TransferPacket> inPacket, BlockingQueue<DatagramPacket> outPacket)
        {
            this.inJobs= handlerJ;
            this.inPacket= inPacket;
            this.outPacket= outPacket;
        }
    }

    BlockingQueue<TransferJob> jobs;                                //Job Input from Node
    BlockingQueue<TransferPacket> inputSocket;                      //Input from the Socket
    BlockingQueue<DatagramPacket> outputSocket;                     //Output to the socket
    Map<InetAddress, HandlerData> handlerData;                      //Matches a target with a thread and it's corresponding data
    KeyPair keyPair;                                                //Security KeyPair
    ThreadControl tc;

    TransferClient ()
    {

    }

    public void run ()
    {
        while (this.tc.get_running())
        {
            try
            {
                TransferJob j= jobs.take();
                BlockingQueue<TransferJob> q;

                //If doesn't exist create and assign Handler Thread
                if (!this.handlerData.containsKey(j.target))
                {
                    q= new LinkedBlockingQueue<>();
                    this.handlerData.put(j.target, new HandlerData(q, inputSocket, outputSocket));
                    Thread t= new Thread(new TransferClientHandle(j.target, q, inputSocket, outputSocket, tc, keyPair));
                }
                //Get the corresponding q to insert
                else
                {
                    q= this.handlerData.get(j.target).inJobs;
                }

                q.add(j);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
