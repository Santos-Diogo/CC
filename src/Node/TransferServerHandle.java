package Node;

import ThreadTools.ThreadControl;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

import Network.UDP.Socket.SocketManager.UserData;
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
    private UserData user_data;
    private FileBlockInfo fbi;
    private String dir;

    TransferServerHandle (ThreadControl tc, Network.UDP.Socket.SocketManager manager, FileBlockInfo fbi, String dir, InetAddress target)
    {
        this.tc= tc;
        this.user_data= manager.registerUser(target);
        this.fbi = fbi;
        this.dir = dir;
    }

    public void handleGet (TransferPacket packet)
    {
        try
        {
            GETPayload getPacket= (GETPayload) packet;

            //File and Blocks to send
            long fileID= getPacket.file;
            String file = fbi.getFile_byID(fileID);
            List<Long> blocks= getPacket.blocks;
            blocks.sort(null);

            //Send all the blocks

            String fileDir= dir + file;
            
            if (fbi.hasWholeFile(file))
            {
                try (FileInputStream fileInputStream = new FileInputStream(fileDir))
                {
                    for (Long b : blocks)
                    {
                        long skipBytes = (long) b * Shared.Defines.blockSize;
                        if (fileInputStream.skip(skipBytes) == skipBytes) 
                        {
                            byte[] block = new byte[Shared.Defines.blockSize];
                            fileInputStream.read(block);
                            TSFPayload rep_packet= new TSFPayload(b, block, TypeMsg.TSF);
                            user_data.user_connection.addPacketTransmission(user_data.user_id, rep_packet);
                        }
                    }
                }
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            } 
            else 
            {
                for(Long b : blocks)
                {
                    String blockFileDir = fileDir + ".fsblk." + b;
                    byte[] block = Files.readAllBytes(Path.of(blockFileDir));
                    TSFPayload rep_packet= new TSFPayload(b, block, TypeMsg.TSF);
                    user_data.user_connection.addPacketTransmission(user_data.user_id, rep_packet);
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
                throw new Exception("Fodeu-se");
        }
    }

    public void run ()
    {
        BlockingQueue<TransferPacket> input_queue =user_data.user_connection.getInput(user_data.user_id);

        while (this.tc.get_running())
        {
            try 
            {
                TransferPacket p= input_queue.take();
                handle (p);
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }       
}
