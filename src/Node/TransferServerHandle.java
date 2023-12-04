package Node;

import ThreadTools.ThreadControl;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import Network.UDP.Socket.SocketManager.IOQueue;
import Network.UDP.TransferProtocol.TransferPacket;
import Network.UDP.TransferProtocol.TransferPayload.GETPayload;
import Shared.Crypt;

import java.security.KeyPair;
import java.util.List;
import java.util.Map;

import Blocker.FileBlockInfo;

import java.io.FileInputStream;
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
    private FileBlockInfo fbi;
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

    TransferServerHandle (ThreadControl tc, TransferPacket p, Network.UDP.Socket.SocketManager manager, KeyPair keyPair, FileBlockInfo fbi, String dir)
    {
        try
        {
            this.tc= tc;
            this.target= p.source;
            this.ioQueue= manager.getQueue(manager.register());
            this.keyPair= keyPair;
            this.fbi = fbi;
            this.dir = dir;

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
            long fileID= getPacket.file;
            String file = fbi.getFile_byID(fileID);
            List<Long> blocks= getPacket.blocks;
            blocks.sort(null);

            //Send all the blocks

            String fileDir= dir + file;
            
            if (fbi.hasWholeFile(file))
            {
                try (FileInputStream fileInputStream = new FileInputStream(fileDir)){
                    for (Long b : blocks)
                    {
                        long skipBytes = (long) b * Shared.Defines.blockSize;
                        if (fileInputStream.skip(skipBytes) == skipBytes) 
                        {
                            byte[] block = new byte[Shared.Defines.blockSize];
                            fileInputStream.read(block);
                            
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                
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
            try {
                TransferPacket p= this.ioQueue.in.take();
                handle (p);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }       
}
