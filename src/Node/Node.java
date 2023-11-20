package Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import Shared.Net_Id;
import Track_Protocol.*;
import Track_Protocol.TrackPacket.TypeMsg;

/***
 * Main Node thread
 */
public class Node 
{
    private static ObjectOutputStream trackerOutput;
    private static ObjectInputStream trackerInput;
    private static InetAddress adress;
    private static Scanner scanner = new Scanner(System.in);

    private static void handle_avf() 
    {
        try 
        {
            // Write Request
            trackerOutput.writeObject(new TrackPacket(new Net_Id(adress), TypeMsg.AVF_REQ));
            trackerOutput.flush();

            // Get response
            AvfRepPacket packet = (AvfRepPacket) trackerInput.readObject();

            // Write File Names
            List<String> files = packet.get_files();
            for (String s : files) 
            {
                System.out.println(s);
            }
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
    }

    private static void handle_command(String command) 
    {
        switch (command) 
        {
            case "avf":
                handle_avf();
                break;
            default:
                break;
        }
    }

    private static String command_request() 
    {
        System.out.println("Type your desired command:\navf - available files\nquit- exit the network\n");
        return scanner.nextLine();
    }

    public static void main(String[] args) throws InterruptedException 
    {
        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try 
        {
            // Define this machine IP adress
            adress = Inet4Address.getLocalHost();
            // Connects to server
            Socket socket = new Socket(serverAddress, serverPort);
            trackerOutput = new ObjectOutputStream(socket.getOutputStream());
            trackerInput = new ObjectInputStream(socket.getInputStream());
            // Send Reg message
            NodeInfo ndinfo = new NodeInfo(args[2]);
            trackerOutput.writeObject(new RegPacket(new Net_Id(adress), TypeMsg.REG, ndinfo.get_file_blocks()));
            trackerOutput.flush();
            
            // Handle commands
            String command;
            while (!(command = command_request()).equals("quit")) 
            {
                handle_command(command);
            }

            // Close the socket when done
            socket.close();
        } 
        catch (UnknownHostException e) 
        {
            System.out.println(serverAddress + " Is not a valid adress");
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        // Tem de ter um Node-Handler para receber os pedidos dos outros nodes e dedicar
        // a cada um uma instancia de Node_Communication
    }
}
