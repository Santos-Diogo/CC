package Node;

import java.net.InetAddress;
import java.util.List;

import Network.UDP.Socket.SocketManager.IOQueue;

public class Transfer implements Runnable{

    private InetAddress node_toRequest; 
    private long id;
    private IOQueue udpQueue;
    private long fileid;
    private List<Long> blocks;

    public Transfer (Network.UDP.Socket.SocketManager udpManager, InetAddress node, long fileid, List<Long> blocks)
    {
        this.node_toRequest = node;
        this.id = udpManager.register();
        this.udpQueue = udpManager.getQueue(this.id);
        this.fileid = fileid;
        this.blocks = blocks;
    }


    public void run ()
    {
        

    }
    
}
