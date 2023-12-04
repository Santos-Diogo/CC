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
import Network.UDP.TransferProtocol.TransferPacket;
import Shared.NetId;
import Shared.NodeBlocks;
import Shared.Tuple;
import ThreadTools.ConcurrentInputStream;
import ThreadTools.*;
import Network.TCP.Socket.SocketManager;

/***
 * Main Node thread
 */
public class Node
{
    private static long trackerId;                                      //Id assigned by the manager
    private static BlockingQueue<TrackPacket> trackerInput;             //TCP Input
    private static BlockingQueue<TrackPacket> trackerOutput;            //TCP Output
    private static Network.TCP.Socket.SocketManager tcpSocketManager;   //TCP socket manager

    private static Network.UDP.Socket.SocketManager udpSocketManager;   //UDP socket manager

    private static NetId net_Id;                                        //Self NetId
    private static Scanner scanner = new Scanner(System.in);            //Console input
    private static Map<String, Long> filesId;                           //Matches the files name with their id in the server context
    private static FileBlockInfo fbInfo;                                //Info on the node
    private static ThreadControl tc= new ThreadControl();               //Object used to terminate minor threads
    public static DNScache dnscache = new DNScache();

    
    
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
     * Temporary solution
     */
    private static void handle_get(String file) 
    {
        try 
        {
            // Send Repply
            trackerOutput.add();

            //Get Response
            GetRepPacket resp = trackerInput.take();

            //Debug
            Set<NetId> nodes = resp.get_nodeBlocks().get_nodes();

            System.out.println("Nodes:");
            for (NetId n : nodes) 
            {
                System.out.println(n.getName());
            }
            //Debug end

            Map<Long, NetId> blockNode= scalonate (resp.get_nodeBlocks(), resp.get_nBlocks(), resp.getWorkLoad(), resp.getOwnedBlocks());
            //Debug start
            for (Map.Entry<NetId, Integer> wkl : resp.getWorkLoad().entrySet())
                System.out.println(wkl.getKey().toString() + ", " + wkl.getValue());
            //Debug end

            //Here we need to update tracker about the workload thats being issued on the nodes and to start the transfer process
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
        switch (command.split("\\s+")[0]) {
            case "avf":
                handle_avf();
                break;
            case "get":
                try {
                    handle_get(command.split("\\s+")[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Please specify the file to download");
                    break;
                }
            case "quit":
                handle_quit();
                break;
            default:
                break;
        }
    }

    
    /** 
     * @return String
     */
    private static String command_request() {
        System.out.println("\nType your desired command:\navf - available files\nget <filename> - download file 'filename'\nquit- exit the network\n");
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

            // Creates TCP Manager
            socket= new Socket(serverAddress, serverPort);
            tcpSocketManager= new SocketManager(socket, tc);
            trackerId= tcpSocketManager.register();
            trackerInput= tcpSocketManager.getInputQueue(trackerId);
            trackerOutput= tcpSocketManager.getOutpuQueue();

            // Creates UDP Manager
            udpSocketManager= new Network.UDP.Socket.SocketManager(tc);

            //SetsUp UDP_Client and UDP_Server 
            Thread udpC= new Thread(new TransferRequests());
            Thread udpS= new Thread(new TransferServer());
            udpC.start();
            udpS.start();

            //Registers Self
            fbInfo= new FileBlockInfo(args[0]);
            filesId= register (fbInfo);


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
            scanner.close();
        }
    }
}