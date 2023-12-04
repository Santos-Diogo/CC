package Node;

import Blocker.FileBlockInfo;
import Network.UDP.Socket.SocketManager.IOQueue;
import ThreadTools.ThreadControl;

public class TransferServer implements Runnable
{
    private FileBlockInfo fbi;
    private ThreadControl tc;
    private IOQueue ioQueue;

    TransferServer (FileBlockInfo fbi, Network.UDP.Socket.SocketManager manager, ThreadControl tc)
    {
        this.fbi= fbi;
        this.tc= tc;

        long udpID= manager.registerServer();
        this.ioQueue = manager.getQueue(udpID);
    }

    public void run ()
    {
        while (this.tc.get_running())
        {
            
        }
    }
}
