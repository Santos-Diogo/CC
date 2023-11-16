package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import SharedState.SharedState;
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
    private Socket socket;
    private ObjectInputStream input;
    private SharedState ss;

    private void handle(TrackPacket packet)
    {
        switch (packet.getType()) 
        {
            case REG:
            {
                System.out.println("REG message");
                RegPacket p= (RegPacket) packet.getPayload();
                //Lidar com o pacote de registo
                break;
            }
            case AVF_REQ:
            {
    
                break; 
            }
            case AVF_RESP:
            {
    
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
            case GET_RESP:
            {    

            }
        }
    }

    public ServerCom(Socket socket, ThreadControl tc, SharedState ss) throws IOException 
    {
        this.socket = socket;
        this.tc = tc;
        this.ss = ss;
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    public void run() 
    {
        TrackPacket packet;

        while (tc.get_running() == true) 
        {
            try 
            {
                packet = (TrackPacket) input.readObject();
            }
            catch (IOException | ClassNotFoundException e) 
            {
                break;
            }
            handle(packet);
        }
    }
}
