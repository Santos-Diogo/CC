package Node;

import java.util.concurrent.BlockingQueue;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import Blocker.*;
import Network.TCP.TrackProtocol.*;
import Network.TCP.TrackProtocol.TrackPacket.TypeMsg;
import Shared.NetId;
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
    private static BlockingQueue<GetRepPacket> files;                   //Info for TransferRequests
    private static Network.TCP.Socket.SocketManager tcpSocketManager;   //TCP socket manager
    private static Network.UDP.Socket.SocketManager udpSocketManager;   //UDP socket manager

    private static NetId net_Id;                                        //Self NetId
    private static Scanner scanner = new Scanner(System.in);            //Console input
    private static FileBlockInfo fbInfo;                                //Info on the node
    private static ThreadControl tc= new ThreadControl();               //Object used to terminate minor threads

    
    
    private static void handle_avf() 
    {
        try 
        {
            // Write Request
            trackerOutput.add(new TrackPacket(net_Id, TypeMsg.AVF_REQ, trackerId, 0));

            // Get response
            AvfRepPacket packet = (AvfRepPacket) trackerInput.take();

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
     * Mudado para o TransferServerHandle (?)
     */
    private static void handle_get(String file) 
    {
        try 
        {
            GetReqPacket request = new GetReqPacket(new TrackPacket(net_Id, TypeMsg.GET_REQ, trackerId, 0) ,file);
            // Send Repply
            trackerOutput.add(request);

            //Get Response
            GetRepPacket resp = (GetRepPacket) trackerInput.take();

            //Debug
            Set<NetId> nodes = resp.get_nodeBlocks().get_nodes();

            System.out.println("Nodes:");
            for (NetId n : nodes) 
            {
                System.out.println(n.getName());
            }
            //Debug end

            //Debug start
            for (Map.Entry<NetId, Integer> wkl : resp.getWorkLoad().entrySet())
                System.out.println(wkl.getKey().toString() + ", " + wkl.getValue());
            //Debug end
            files.add(resp);
            //!!! Here we need to update tracker about the workload thats being issued on the nodes and to start the transfer process!!!
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    private static void handle_quit() 
    {
        trackerOutput.add(new TrackPacket(net_Id, TypeMsg.DC, trackerId, trackerId)); //From e to pelo q percebi são removidos
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
    private static void register(FileBlockInfo b) throws IOException, ClassNotFoundException
    {
        // Send Reg message with Node Status collected by "FileBlockInfo"
        trackerOutput.add(new RegReqPacket(new TrackPacket(net_Id, TypeMsg.REG_REQ, trackerId, 0), b));  //From e to pelo q percebi são removidos
        try{
            RegRepPacket rep= (RegRepPacket) trackerInput.take();
            b.set_FilesID(rep.get_fileId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            udpSocketManager= new Network.UDP.Socket.SocketManager(tc, fbInfo, args[0]);

            //Registers Self
            fbInfo= new FileBlockInfo(args[0]);
            register (fbInfo);

            //SetsUp UDP_Client and UDP_Server 
            Thread udpC= new Thread(new TransferRequests(tc, files, trackerOutput, udpSocketManager));
            Thread udpS= new Thread(new TransferServer(fbInfo, udpSocketManager, tc, args[0]));
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
            scanner.close();
        }
    }
}