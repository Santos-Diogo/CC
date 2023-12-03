package Node;

import ThreadTools.ThreadControl;
import java.util.concurrent.BlockingQueue;
import UDP.*;

/**
 * Thread that handles transfer requests from the node
 */
class TransferRequests implements Runnable
{
    /**
     * Responsible for getting and assembling a file
     */
    private class FileGetter implements Runnable
    {
        long file;
        ThreadControl tc;
        UDP.Socket.SocketManager udpManager;
        //TCP socket Manager

        FileGetter (long file, ThreadControl tc)
        {
            this.file= file;
            this.tc= tc;
        }

        public void run ()
        {
            //Send escalonate Request to TrackerServer -> Requires TCP Socket

            //Create threads for each node to get the packet -> Requeires UDP Socket

            //Assemble the packet
        }
    }

    ThreadControl tc;
    BlockingQueue<Long> files;

    public void run()
    {
        while (tc.get_running())
        {
            try
            {
                //Take file to transfer
                long file= files.take();
                Thread t= new Thread(new FileGetter (file, tc));
                t.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
