package Node;

import ThreadTools.ThreadControl;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import Network.UDP.Socket.SocketManager.UserInfo;
import Network.UDP.TransferProtocol.TransferPacket;
import Network.UDP.TransferProtocol.TransferPacket.TypeMsg;
import Network.UDP.TransferProtocol.TransferPayload.GETPayload;
import Network.UDP.TransferProtocol.TransferPayload.TSFPayload;

import java.security.KeyPair;
import java.util.List;

import Blocker.FileBlockInfo;

import java.io.FileInputStream;

public class TransferServerHandle implements Runnable
{
    private ThreadControl tc;
    private InetAddress target;
    private UserInfo userInfo;
    private KeyPair keyPair;
    private FileBlockInfo fbi;
    private String dir;


    //E para cagar em ti
    public void connect (TransferPacket p) throws Exception
    {
        //Create crypt from first packet
        //this.targetId= p.from;
        //this.crypt= new Crypt(this.keyPair.getPrivate(), p.payload);

        TransferPacket packet= new TransferPacket(this.keyPair.getPublic().getEncoded());
        byte[] serializedPacket= packet.serialize();
        DatagramPacket datagramPacket= new DatagramPacket(serializedPacket, serializedPacket.length, this.target, Shared.Defines.transferPort);

        //Send packet with own public key
        //this.ioQueue.out.add(datagramPacket);
    }

    TransferServerHandle (ThreadControl tc, TransferPacket p, Network.UDP.Socket.SocketManager manager, KeyPair keyPair, FileBlockInfo fbi, String dir)
    {
        try
        {
            this.tc= tc;
            this.target= p.source;
            this.userInfo= manager.register(target);
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
                            TSFPayload payload = new TSFPayload(b, block);
                            TransferPacket tsfpacket = new TransferPacket(TypeMsg.TSF, payload.serialize());
                            userInfo.out.add(tsfpacket);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                for(Long b : blocks)
                {
                    String blockFileDir = fileDir + ".fsblk." + b;
                    byte[] block = Files.readAllBytes(Path.of(blockFileDir));
                    TSFPayload payload = new TSFPayload(b, block);
                    TransferPacket tsfpacket = new TransferPacket(TypeMsg.TSF, payload.serialize());
                    userInfo.out.add(tsfpacket);
                }
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
                TransferPacket p= this.userInfo.in.take();
                handle (p);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }       
}
