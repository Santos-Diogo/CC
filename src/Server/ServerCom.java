package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Blocker.BlockInfo;
import Blocker.FileBlockInfo;
import Network.TCP.TrackProtocol.*;

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
            out.writeObject(new RegRepPacket(selfId, fileId));    
            out.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void handle_AVF_REQ(NetId requesting_node) {
        System.out.println("AVF REQ");
        try 
        {
            out.writeObject(new AvfRepPacket(selfId, serverInfo.get_filesWithSizes(requesting_node)));
            out.flush();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void handle_GET_REQ(TrackPacket packet) {
        // @TODO
        System.out.println("GET message");
        try {
            GetReqPacket p = (GetReqPacket) packet;
            String file = p.getFile();
            long fileId = serverInfo.get_fileId(file);
            long nBlocks = serverInfo.get_nBlocks(file);
            List<Long> ownedBlocks = new ArrayList<>();
            NodeBlocks nodeInfoFile = serverInfo.get_nodeInfoFile(file, p.getNet_Id(), ownedBlocks);
            Map<NetId, Integer> workLoad = serverInfo.get_workLoad(nodeInfoFile.get_nodes());
            GetRepPacket replyP = new GetRepPacket(selfId, fileId, nBlocks, nodeInfoFile, ownedBlocks, workLoad);
            out.writeObject(replyP);
            out.flush();
        } catch (IOException e) {
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
                handle_AVF_REQ(packet.getNet_Id());
                break;
            }
            case ADD_F: {

                break;
            }
            case RM_F: {

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
