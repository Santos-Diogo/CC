package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Blocker.BlockInfo;
import Blocker.FileBlockInfo;
import Network.TCP.TrackProtocol.*;
import Network.TCP.TrackProtocol.TrackPacket.TypeMsg;

import java.lang.String;

import Shared.NetId;
import Shared.NodeBlocks;
import ThreadTools.ThreadControl;

/**
 * Class responsible for handling communication for each independent node on the
 * server side.
 */
public class ServerCom implements Runnable 
{
    private ThreadControl tc;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerInfo serverInfo;
    private NetId selfId;

    public ServerCom(Socket socket, ThreadControl tc, ServerInfo serverInfo, NetId n) throws IOException {
        this.tc = tc;
        this.serverInfo = serverInfo;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.selfId = n;
    }

    private void handle_REG(TrackPacket packet) 
    {
        System.out.println("REG message");
        RegReqPacket p = (RegReqPacket) packet;
        NetId node = packet.netId;
        FileBlockInfo fbi = p.get_fileBlockInfo();
        Map<String, Long> fileId= new HashMap<>();
        BlockInfo blockInfo;
        String fileName;
        serverInfo.register_inLoad(node);
        // We insert each (file_name,blocks[]) and get a correspondig id for our files
        for (Map.Entry<String, BlockInfo> e : fbi.get_fileBlockInfo().entrySet()) 
        {
            blockInfo = e.getValue();
            fileName= e.getKey();
            Long filesize = fbi.get_filesize(fileName);
            if (filesize != null)
                fileId.put(fileName, serverInfo.add_file(fileName, node, blockInfo, filesize));
            else
                fileId.put(fileName, serverInfo.add_file(fileName, node, blockInfo));
        }
        try
        {
            //Write back to node the sent file's ids
            out.writeObject(new RegRepPacket(new TrackPacket(selfId, TypeMsg.REG_REP, 0, packet.from), fileId));    
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void handle_AVF_REQ(TrackPacket packet) 
    {
        System.out.println("AVF REQ");
        try 
        {
            out.writeObject(new AvfRepPacket(new TrackPacket(selfId, TypeMsg.AVF_RESP, 0, packet.from), this.serverInfo.get_filesWithSizes(packet.netId)));
            out.flush();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void handle_ADD (TrackPacket packet)
    {
        System.out.println("UPD message");
        try 
        {
            AddBlockPacket p = (AddBlockPacket) packet;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void handle_GET_REQ(TrackPacket packet) {
        System.out.println("GET message");
        try 
        {
            GetReqPacket p = (GetReqPacket) packet;
            String file = p.getFile();
            long fileId = serverInfo.get_fileId(file);
            long nBlocks = serverInfo.get_nBlocks(file);
            List<Long> ownedBlocks = new ArrayList<>();
            NodeBlocks nodeInfoFile = serverInfo.get_nodeInfoFile(file, p.netId, ownedBlocks);
            Map<NetId, Integer> workLoad = serverInfo.get_workLoad(nodeInfoFile.get_nodes());
            GetRepPacket replyP = new GetRepPacket(new TrackPacket(selfId, TypeMsg.GET_RESP, 0, packet.from), fileId, file ,nBlocks, nodeInfoFile, ownedBlocks, workLoad);
            out.writeObject(replyP);
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle_DC(TrackPacket packet) {
        System.out.println("DC message");
        serverInfo.remove_infoFromNode(packet.netId);
    }

    private void handle(TrackPacket packet) {
        switch (packet.type) {
            case REG_REQ: {
                handle_REG(packet);
                break;
            }
            case AVF_REQ: {
                handle_AVF_REQ(packet);
                break;
            }
            case ADD: {
                handle_ADD(packet);
                break;
            }
            case GET_REQ: {
                handle_GET_REQ(packet);
                break;
            }
            case DC: {
                handle_DC(packet);
                break;
            }
            default: {
                System.out.println("Fodeu-se");
            }
        }
    }

    public void run() {
        TrackPacket packet = null;

        while (tc.get_running() == true) {
            try {
                packet = (TrackPacket) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                break;
            }
            handle(packet);
        }
        handle_DC(packet);
    }
}
