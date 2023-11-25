package Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import Blocker.*;
import Shared.NetId;
import TrackProtocol.*;
import TrackProtocol.TrackPacket.TypeMsg;

/***
 * Main Node thread
 */
public class Node 
{
    private static ObjectOutputStream trackerOutput;
    private static ObjectInputStream trackerInput;
    private static NetId net_Id;
    private static Scanner scanner = new Scanner(System.in);
    private static Map<String, Long> filesId;   //Matches the files name with their id in the server context

    private static void handle_avf() 
    {
        try 
        {
            // Write Request
            trackerOutput.writeObject(new TrackPacket(net_Id, TypeMsg.AVF_REQ));
            trackerOutput.flush();

            // Get response
            AvfRepPacket packet = (AvfRepPacket) trackerInput.readObject();

            // Write File Names
            Map<String, Long> files = packet.get_files();
            System.out.println("Files:");

            //Can be chaged latter (Better values for bigger files)
            for (Map.Entry<String, Long> e : files.entrySet())
            {
                System.out.println(e.getKey()+ (e.getValue()*Shared.Defines.blockSize/1024)+ "kB");
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * Temporary solution
     */
    private static void handle_get() {
        System.out.println("Name of file to transfer:");
        String file = scanner.nextLine();
        try {
            trackerOutput.writeObject(new GetReqPacket(null, file));
            trackerOutput.flush();

            GetRepPacket resp = (GetRepPacket) trackerInput.readObject();
            Set<NetId> nodes = resp.get_nodeBlocks().keySet();

            System.out.println("Nodes:");
            for (NetId n : nodes) {
                System.out.println(n.get_adr().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handle_quit() {
        try {
            trackerOutput.writeObject(new TrackPacket(net_Id, TypeMsg.DC));
            trackerOutput.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Arguments not enough
     */
    private static void handle_command(String command) {
        switch (command) {
            case "avf":
                handle_avf();
                break;
            case "get":
                handle_get();
                break;
            case "quit":
                handle_quit();
                break;
            default:
                break;
        }
    }

    private static String command_request() {
        System.out.println("Type your desired command:\navf - available files\nquit- exit the network\n");
        return scanner.nextLine();
    }

    /**
     * @param b Info on files in the node's directory
     * @return the input file's id in the server
     * @throws IOException
     */
    private static Map<String, Long> register(FileBlockInfo b) throws IOException, ClassNotFoundException
    {
        // Send Reg message with Node Status collected by "FileBlockInfo"
        trackerOutput.writeObject(new RegReqPacket(net_Id, b));
        trackerOutput.flush();
        RegRepPacket rep= (RegRepPacket) trackerInput.readObject();
        return rep.get_fileId();
    }

    public static void main(String[] args) throws InterruptedException {
        String serverAddress = args[1];
        int serverPort = (args.length > 2) ? Integer.parseInt(args[2]) : Shared.Defines.trackerPort;

        try {
            // Define this machine IP adress
            net_Id = new NetId(Inet4Address.getLocalHost());

            // Connects to server
            Socket socket = new Socket(serverAddress, serverPort);
            trackerOutput = new ObjectOutputStream(socket.getOutputStream());
            trackerInput = new ObjectInputStream(socket.getInputStream());

            //Registers Self
            filesId= register (new FileBlockInfo(args[0]));

            // Initiate NodeHost

            // Handle commands
            String command;
            while (!(command = command_request()).equals("quit")) {
                handle_command(command);
            }
            handle_command("quit");
            // Close the socket when done
            socket.close();
        } catch (UnknownHostException e) {
            System.out.println(serverAddress + " Is not a valid adress");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tem de ter um Node-Handler para receber os pedidos dos outros nodes e dedicar
        // a cada um uma instancia de Node_Communication
    }
}
