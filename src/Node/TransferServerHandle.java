package Node;

import ThreadTools.ThreadControl;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import Network.UDP.Socket.SocketManager.IOQueue;
import Network.UDP.TransferProtocol.TransferPacket;
import Network.UDP.TransferProtocol.TransferPayload.GETPayload;
import Shared.Crypt;

import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;

public class TransferServerHandle implements Runnable
{
    private ThreadControl tc;
    private InetAddress target;
    private long targetId;
    private long selfId;
    private IOQueue ioQueue;
    private KeyPair keyPair;
    private Crypt crypt;
    private String file;
    private String dir;

    public void connect (TransferPacket p) throws Exception
    {
        //Create crypt from first packet
        this.targetId= p.from;
        this.crypt= new Crypt(this.keyPair.getPrivate(), p.payload);

        TransferPacket packet= new TransferPacket(this.keyPair.getPublic().getEncoded());
        byte[] serializedPacket= packet.serialize();
        DatagramPacket datagramPacket= new DatagramPacket(serializedPacket, serializedPacket.length, this.target, Shared.Defines.transferPort);

        //Send packet with own public key
        this.ioQueue.out.add(datagramPacket);
    }

    TransferServerHandle (ThreadControl tc, TransferPacket p, Network.UDP.Socket.SocketManager manager, KeyPair keyPair)
    {
        try
        {
            this.tc= tc;
            this.target= p.source;
            this.ioQueue= manager.getQueue(manager.register());
            this.keyPair= keyPair;

            //connect to client that sent 1st packet
            connect(p);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void handleGet (TransferPacket packet)
    {
        try
        {
            GETPayload getPacket= new GETPayload(packet.payload);

            //File and Blocks to send
            long file= getPacket.file;
            List<Long> blocks= getPacket.blocks;

            //Send all the blocks

            String fileDir= file+ dir;
            InputStream inputStream= Files.newInputStream(Paths.get(fileDir));
            
            long currentOffset= 0;
            for (Long b : blocks)
            {
                 //ATE> AMANAHA    
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void handle (TransferPacket packet) throws Exception
    {
        switch (packet.type)
        {
            case GET:
                handleGet(packet);
            default:
                throw new Exception("Impossible Packet in Server Handler");
        }
    }

    public void run ()
    {
        while (this.tc.get_running())
        {
            TransferPacket p= this.ioQueue.in.take()
            handle (p);
        }
    }       
}
