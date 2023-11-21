package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Blocker.FileBlockInfo;

import java.lang.String;

import Shared.NetId;
import ThreadTools.ThreadControl;
import TrackProtocol.*;

/**
 * Class responsible for handling communication for each independent node on the
 * server side.
 */
public class ServerCom implements Runnable 
{
    
    private ThreadControl tc;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private FileBlockInfo serverInfo;
    private NetId n;

    public ServerCom(Socket socket, ThreadControl tc, FileBlockInfo serverInfo, NetId n) throws IOException 
    {
        this.tc = tc;
        this.serverInfo = serverInfo;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.n= n;
    }

    private void handle_REG(TrackPacket packet) 
    {
        System.out.println("REG message");
        RegPacket p = (RegPacket) packet;
        NetId node= packet.getNet_Id();
        // We insert each (file_name,blocks[])

        for () 
        {
            serverInfo.add_file(e.getKey(), node, e.getValue());
        }
    }

    private void handle_AVF_REQ() 
    {
        System.out.println("AVF REQ");
        try 
        {
            out.writeObject(new AvfRepPacket (n, serverInfo.get_files()));
            out.flush();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private void handle_GET_REQ(TrackPacket packet)
    {
        System.out.println("GET message");
        GetReqPacket p= (GetReqPacket) packet;

        //Get number of blocks and BlockNodes
        int nBlocks= serverInfo.nBlocks(p.getFile());
        


        out.writeObject(new GetRepPacket(n, , null));
    }

    private void handle(TrackPacket packet) 
    {
        switch (packet.getType()) 
        {
            case REG: 
            {
                handle_REG(packet);
                break;
            }
            case AVF_REQ: 
            {
                handle_AVF_REQ();
                break;
            }
            case ADD_F: 
            {

                break;
            }
            case RM_F: 
            {

                break;
            }
            case GET_REQ: 
            {
                handle_GET_REQ(packet);
                break;
            }
            default: 
            {
                System.out.println("Fodeu-se");
            }
        }
    }


    public void run()
    {
        TrackPacket packet;

        while (tc.get_running() == true) 
        {
            try 
            {
                packet = (TrackPacket) in.readObject();
            } 
            catch (IOException | ClassNotFoundException e) 
            {
                break;
            }
            handle(packet);
        }
    }
}
