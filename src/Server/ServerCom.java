package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.String;

import ThreadTools.ThreadControl;
import Track_Protocol.TrackPacket;
import Payload.TrackPacketPayload.*;

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

    private void handle_REG (TrackPacket packet)
    {
        System.out.println("REG message");
        RegPacket p= (RegPacket) packet.getPayload();

        InetAddress srcAddress= packet.getSrc_ip();
        //We insert each (file_name,blocks[])
        for (Map.Entry<String,List<Integer>> e : p.get_files_blocks().entrySet())
        {
            serverInfo.add_file(e.getKey(), srcAddress, e.getValue());
        }
    }

    private void handle_AVF_REQ (TrackPacket packet)
    {
        System.out.println("AVF REQ");
        try
        {
            out.writeObject(new AvfRepPacket(serverInfo.get_files()));
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
                handle_AVF_REQ(packet);   
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
    
    public ServerCom(Socket socket, ThreadControl tc, ServerInfo serverInfo) throws IOException 
    {
        this.tc= tc;
        this.serverInfo= serverInfo;
        this.in= new ObjectInputStream(socket.getInputStream());
        this.out= new ObjectOutputStream(socket.getOutputStream());
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
