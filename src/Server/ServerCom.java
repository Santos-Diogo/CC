package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import java.lang.String;

import Shared.Net_Id;
import ThreadTools.ThreadControl;
import Track_Protocol.*;
import Track_Protocol.TrackPacket.TypeMsg;

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
    private Net_Id n;

    public ServerCom(Socket socket, ThreadControl tc, ServerInfo serverInfo, Net_Id n) throws IOException 
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
        Net_Id node= packet.getNode();
        // We insert each (file_name,blocks[])

        for (Map.Entry<String, List<Integer>> e : p.get_files_blocks().entrySet()) 
        {
            serverInfo.add_file(e.getKey(), node, e.getValue());
        }
    }

    private void handle_AVF_REQ() 
    {
        System.out.println("AVF REQ");
        try 
        {
            out.writeObject(new AvfRepPacket (n, TypeMsg.AVF_RESP, serverInfo.get_files()));
            out.flush();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
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
