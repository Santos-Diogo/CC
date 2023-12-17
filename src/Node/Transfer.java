package Node;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import Network.TCP.TrackProtocol.TrackPacket;
import Network.UDP.Socket.SocketManager.IOQueue;
import Network.UDP.TransferProtocol.TransferPacket;
import Network.UDP.TransferProtocol.TransferPacket.TypeMsg;
import Network.UDP.TransferProtocol.TransferPayload.GETPayload;
import Network.UDP.TransferProtocol.TransferPayload.TSFPayload;
import Shared.Crypt;

public class Transfer implements Runnable{

    private InetAddress node_toRequest; 
    private long udpId;
    private IOQueue udpQueue;
    private BlockingQueue<TrackPacket> tcpQueue;
    private long fileid;
    private List<Long> blocks;
    private Crypt crypt;
    private long target_id;

    public Transfer (Network.UDP.Socket.SocketManager udpManager, Network.TCP.Socket.SocketManager tcpManager, InetAddress node, long fileid, List<Long> blocks)
    {
        this.node_toRequest = node;
        this.udpId = udpManager.register();
        this.udpQueue = udpManager.getQueue(this.udpId);
        this.tcpQueue = tcpManager.getOutpuQueue();
        this.fileid = fileid;
        this.blocks = blocks;
    }


    public void run ()
    {
        GETPayload payload = new GETPayload(fileid, blocks);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            DataOutputStream dos = new DataOutputStream(baos);
            payload.serialize(dos);
            dos.flush();
            TransferPacket packet = new TransferPacket(TypeMsg.GET, udpId, target_id, crypt.encrypt(baos.toByteArray()));
            baos.reset();
            packet.serialize(dos); 
            byte[] serialized_packet = baos.toByteArray();
            DatagramPacket packet2 = new DatagramPacket(serialized_packet, serialized_packet.length, node_toRequest, Shared.Defines.transferPort);
            udpQueue.out.add(packet2);
            int blocks_size = blocks.size();
            for(int i = 0; i < blocks_size; i++)
            {
                TransferPacket repPacket = udpQueue.in.take();
                TSFPayload file_block = new TSFPayload(crypt.decrypt(repPacket.payload));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
