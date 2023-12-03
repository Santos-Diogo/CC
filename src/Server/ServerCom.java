package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Blocker.BlockInfo;
import Network.TCP.TrackProtocol.*;

import java.lang.String;

import Shared.NetId;
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
        NetId node = packet.net_Id;
        
        Map<String, Long> fileId= new HashMap<>();
        BlockInfo nBlock;
        String fileName;
        // We insert each (file_name,blocks[]) and get a correspondig id for our files
        for (Map.Entry<String, BlockInfo> e : p.get_fileBlockInfo().get_fileBlockInfo().entrySet()) 
        {
            nBlock = e.getValue();
            fileName= e.getKey();
            fileId.put(fileName, serverInfo.add_file(fileName, node, nBlock));
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

    private void handle_AVF_REQ() {
        System.out.println("AVF REQ");
        try 
        {
            out.writeObject(new AvfRepPacket(selfId, serverInfo.get_filesWithSizes()));
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
            GetRepPacket replyP = new GetRepPacket( selfId,
                                                    serverInfo.get_fileId(file),
                                                    serverInfo.get_nBlocks(file),
                                                    serverInfo.get_nodeInfoFile(file));
            out.writeObject(replyP);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle_DC(TrackPacket packet) {
        System.out.println("DC message");
        serverInfo.remove_infoFromNode(packet.net_Id);
    }

    private void handle(TrackPacket packet) {
        switch (packet.type) {
            case REG_REQ: {
                handle_REG(packet);
                break;
            }
            case AVF_REQ: {
                handle_AVF_REQ();
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
