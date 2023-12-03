package Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import Blocker.*;
import Shared.NetId;
import Shared.NodeBlocks;
import Shared.Tuple;
import ThreadTools.ThreadControl;
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
    private static FileBlockInfo fbInfo;
    private static ThreadControl tc= new ThreadControl();



    /**
     * This only solves the transfer without scalonation
     * @param nodeBlocks
     * @param nBlocks
     * @return
     */
    private static Map<Long, NetId> scalonate (NodeBlocks nodeBlocks, long nBlocks, Map<NetId, Integer> workload) throws Exception
    {
        double max_perNode = 0.3;
        Map <Long, NetId> m= new HashMap<>();
        List<Tuple<Long, Integer>> rarestBlocks = nodeBlocks.rarestBlocks(nBlocks);

        //Debug start
        for(Map.Entry<NetId, Integer> wkl: workload.entrySet())
            System.out.println(wkl.getKey().toString() + ", " + wkl.getValue());
	    for(Tuple<Long, Integer> tpl : rarestBlocks)
            System.out.println(tpl.toString());
        //Debug end
        for(Tuple<Long, Integer> block : rarestBlocks)
        {
            NetId node = schedule(block, (int)(nBlocks * max_perNode), nodeBlocks, workload);
            m.put(block.fst(), node);
            int tmp = workload.get(node);
            workload.put(node, tmp + 1);
        }
        //Debug start
        for(Map.Entry<NetId, Integer> wkl: workload.entrySet())
            System.out.println(wkl.getKey().toString() + ", " + wkl.getValue());
        for(Map.Entry<Long, NetId> asd: m.entrySet())
            System.out.println(asd.getKey() + ", " + asd.getValue().toString());
        //Debug end
        return m;
    }

    private static NetId schedule (Tuple<Long, Integer> block, Integer maxBlock_perNode, NodeBlocks nodeBlocks, Map<NetId, Integer> workload)
    {
        if(block.snd() == 1)
            return nodeBlocks.get_loneBlock(block.fst());
        List<NetId> nodes = nodeBlocks.get_nodesBlock(block.fst());
        nodes.sort(Comparator.comparingInt(workload :: get).thenComparing(node -> new Random().nextInt()));
        return nodes.get(0);
    }

    
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
    private static void handle_get() 
    {
        System.out.println("Name of file to transfer:");
        String file = scanner.nextLine();
        try 
        {
            // Send Repply
            trackerOutput.writeObject(new GetReqPacket(null, file));
            trackerOutput.flush();

            //Get Response
            GetRepPacket resp = (GetRepPacket) trackerInput.readObject();

            //Debug
            Set<NetId> nodes = resp.get_nodeBlocks().get_nodes();

            System.out.println("Nodes:");
            for (NetId n : nodes) 
            {
                System.out.println(n.getName());
            }
            //Debug end

            Map<Long, NetId> blockNode= scalonate (resp.get_nodeBlocks(), resp.get_nBlocks(), resp.getWorkLoad());
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

    
    /** 
     * @return String
     */
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
        Socket socket;
        try 
        {
            // Define this machine IP adress
            net_Id = new NetId(InetAddress.getLocalHost().getHostName());

            // Connects to server
            socket = new Socket(serverAddress, serverPort);
            trackerOutput = new ObjectOutputStream(socket.getOutputStream());
            trackerInput = new ObjectInputStream(socket.getInputStream());

            //Registers Self
            fbInfo= new FileBlockInfo(args[0]);
            filesId= register (fbInfo);

            //SetsUp UDP Server
            Thread udpServer= new Thread(new NodeUDP_Server (fbInfo, tc));
            udpServer.start();

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
