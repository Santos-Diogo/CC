package Node;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import Blocker.*;
import Network.TCP.TrackProtocol.*;
import Network.TCP.TrackProtocol.TrackPacket.TypeMsg;
import Shared.NetId;
import ThreadTools.ConcurrentInputStream;
import ThreadTools.*;
import Network.TCP.Socket.SocketManager;

/***
 * Main Node thread
 */
public class Node
{
    private static BlockingQueue<TrackPacket> trackerInput;
    private static BlockingQueue<TrackPacket> trackerOutput;
    private static Network.TCP.Socket.SocketManager tcpSocketManager;   //TCP socket manager
    private static Network.UDP.Socket.SocketManager udpSocketManager;   //UDP socket manager
    private static NetId net_Id;                                        //Self NetId
    private static Scanner scanner = new Scanner(System.in);            //Console input
    private static Map<String, Long> filesId;                           //Matches the files name with their id in the server context
    private static FileBlockInfo fbInfo;                                //Info on the node
    private static ThreadControl tc= new ThreadControl();               //Object used to terminate minor threads

    private static void handle_avf() 
    {
        try 
        {
            // Write Request
            trackerOutput.add();

            // Get response
            AvfRepPacket packet = trackerInput.take();

            // Write File Names
            Map<String, Long> files = packet.get_files();
            System.out.println("Files:");

            //Can be chaged latter (Better values for bigger files)
            for (Map.Entry<String, Long> e : files.entrySet())
            {
                System.out.println(e.getKey() + " Size ~ " + (e.getValue()*Shared.Defines.blockSize/1024)+ "kB");
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * This only solves the transfer without scalonation
     * @param nodeBlocks
     * @param nBlocks
     * @return
     */
    private static Map<Long, NetId> scalonate (Map<NetId, List<Long>> nodeBlocks, long nBlocks) throws Exception
    {
        Map <Long, NetId> m= new HashMap<>();

        for (long i= 0; i< nBlocks; i++)
        {
            NetId id= null;
            for (Map.Entry<NetId, List<Long>> entry : nodeBlocks.entrySet())
            {
                if (entry.getValue() == null || entry.getValue().contains(i))
                {
                    id= entry.getKey();
                    break;
                }
            }

            //Not a single Node owns a given block
            if (id== null)
                throw new Exception("Transfer not possible");

            //Add the node to the scalonation list
            m.put(i, id);
        }

        return m;
    }

    /**
     * Temporary solution
     */
    private static void handle_get() 
    {
        System.out.println("Name of file to transfer:");
        String file = scanner.nextLine();
        try 
        {
            // Send Repply
            trackerOutput.add();

            //Get Response
            GetRepPacket resp = trackerInput.take();

            //Debug
            Set<NetId> nodes = resp.get_nodeBlocks().keySet();

            System.out.println("Nodes:");
            for (NetId n : nodes) 
            {
                System.out.println(n.getName());
            }
            //Debug end

            //Scalonate nodes
            Map<Long, NetId> blockNode= scalonate (resp.get_nodeBlocks(), resp.get_nBlocks());

            //Send scalonation to UDP Task Queue

        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    private static void handle_quit() 
    {
        try 
        {
            trackerOutput.add();
        }
        catch (IOException e) 
        {
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

    private static String command_request() 
    {
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
        trackerOutput.add();
        RegRepPacket rep= trackerInput.take();
        return rep.get_fileId();
    }

    public static void main(String[] args) throws InterruptedException 
    {
        String serverAddress = args[1];
        int serverPort = (args.length > 2) ? Integer.parseInt(args[2]) : Shared.Defines.trackerPort;
        Socket socket;
        try 
        {
            // Define this machine IP adress
            net_Id = new NetId(InetAddress.getLocalHost().getHostName());

            // Creates UDP Manager


            // Creates TCP Manager
            socket = new Socket(serverAddress, serverPort);
            trackerOutput = new ConcurrentOutputStream(new ObjectOutputStream(socket.getOutputStream()));
            trackerInput = new ConcurrentInputStream(new ObjectInputStream(socket.getInputStream()));

            //Registers Self
            fbInfo= new FileBlockInfo(args[0]);
            filesId= register (fbInfo);


            //SetsUp UDP_Client and UDP_Server 
            Thread udpC= new Thread(new UDP_Client());
            Thread udpS= new Thread(new UDP_Server());
            udpC.start();
            udpS.start();


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
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally 
        {
            //Terminate all minor threads
            tc.set_running(false);
        }
    }
}