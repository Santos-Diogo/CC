package Node;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import Network.UDP.Socket.SocketManager.UserData;
import Network.UDP.TransferProtocol.TransferPacket;
import Network.UDP.TransferProtocol.TransferPacket.TypeMsg;
import Network.UDP.TransferProtocol.TransferPayload.GETPayload;
import Network.UDP.TransferProtocol.TransferPayload.TSFPayload;

public class Transfer implements Runnable{

    private UserData userData;
    private BlockingQueue<TSFPayload> queue;
    private long fileid;
    private List<Long> blocks;

    public Transfer (Network.UDP.Socket.SocketManager udpManager, InetAddress node, long fileid, List<Long> blocks, BlockingQueue<TSFPayload> queue)
    {
        this.userData = udpManager.registerUser(node);
        this.queue = queue;
        this.fileid = fileid;
        this.blocks = blocks;
    }


    public void run ()
    {
        GETPayload payload = new GETPayload(fileid, blocks, TypeMsg.GET);
        try {
            userData.user_connection.addPacketTransmission(userData.user_id, payload);
            int blocks_size = blocks.size();
            BlockingQueue<TransferPacket> transfers = userData.user_connection.getInput(userData.user_id);
            for(int i = 0; i < blocks_size; i++)
            {
                TSFPayload repPacket = (TSFPayload) transfers.take();
                queue.add(repPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
