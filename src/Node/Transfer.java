package Node;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import Network.TCP.TrackProtocol.TrackPacket;
import Network.UDP.Socket.SocketManager.UserData;
import Network.UDP.TransferProtocol.TransferPacket;
import Network.UDP.TransferProtocol.TransferPacket.TypeMsg;
import Network.UDP.TransferProtocol.TransferPayload.GETPayload;
import Network.UDP.TransferProtocol.TransferPayload.TSFPayload;
import Shared.Crypt;

public class Transfer implements Runnable{

    private InetAddress node_toRequest; 
    private UserData userData;
    private BlockingQueue<TrackPacket> tcpQueue;
    private long fileid;
    private List<Long> blocks;
    private Crypt crypt;
    private long target_id;

    public Transfer (Network.UDP.Socket.SocketManager udpManager, BlockingQueue<TrackPacket> tcpQueue, InetAddress node, long fileid, List<Long> blocks)
    {
        this.node_toRequest = node;
        this.userData = udpManager.registerUser(node);
        this.tcpQueue = tcpQueue;
        this.fileid = fileid;
        this.blocks = blocks;
    }


    public void run ()
    {
        GETPayload payload = new GETPayload(fileid, blocks, TypeMsg.GET);
        try {
            userData.user_connection.addPacketTransmission(userData.user_id, payload);
            int blocks_size = blocks.size();
            for(int i = 0; i < blocks_size; i++)
            {
                TransferPacket repPacket = userData.user_connection
                TSFPayload file_block = new TSFPayload(crypt.decrypt(repPacket.payload));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
