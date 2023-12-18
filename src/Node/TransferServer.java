package Node;

import java.util.Map;

import Blocker.FileBlockInfo;
import Network.UDP.Socket.SocketManager.IOQueue;
import ThreadTools.ThreadControl;

public class TransferServer implements Runnable
{
    private FileBlockInfo fbi;
    private ThreadControl tc;
    private IOQueue ioQueue;
    private String dir;

    TransferServer (FileBlockInfo fbi, Network.UDP.Socket.SocketManager manager, ThreadControl tc, String dir)
    {
        this.fbi = fbi;
        this.tc= tc;
        this.dir = dir;
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
