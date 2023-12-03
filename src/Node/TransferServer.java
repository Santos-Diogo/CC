package Node;

import Blocker.FileBlockInfo;
import ThreadTools.ThreadControl;

public class TransferServer implements Runnable
{
    private FileBlockInfo fbi;
    private long udpID;
    private ThreadControl tc;

    TransferServer (FileBlockInfo fbi, Network.UDP.Socket.SocketManager manager, ThreadControl tc)
    {
        this.fbi= fbi;
        this.udpID= manager.register();
        this.tc= tc;
    }

    public void run ()
    {

    }
}
