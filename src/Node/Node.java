package Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
import TransferProtocol.GetFilesReq;
import TransferProtocol.TransferPacket;

/***
 * Main Node thread
 */
public class Node 
{
    private static ObjectOutputStream trackerOutput;
    private static ObjectInputStream trackerInput;
    private static NetId net_Id;
    private static Scanner scanner = new Scanner(System.in);
    private static FileBlockInfo fbInfo;
    private static ThreadControl tc= new ThreadControl();


    public static void remove_ownedBlocks(List<Tuple<Long, Integer>> blocks, List<Long> ownedBlocks) {
        
        Iterator<Tuple<Long, Integer>> iterator = blocks.iterator();

        while (iterator.hasNext()) {
            Tuple<Long, Integer> block = iterator.next();

            if (ownedBlocks.contains(block.fst())) {
                iterator.remove(); // Remove the element from the list
            }
        }
    }

    /**
     * This only solves the transfer without scalonation
     * @param nodeBlocks
     * @param nBlocks
     * @return
     */
    private static Map<NetId, List<Long>> scalonate (NodeBlocks nodeBlocks, long nBlocks, Map<NetId, Integer> workload, List<Long> ownedBlocks)
    {
        Map <NetId, List<Long>> m= new HashMap<>();
        List<Tuple<Long, Integer>> rarestBlocks = nodeBlocks.rarestBlocks(nBlocks);
        remove_ownedBlocks(rarestBlocks, ownedBlocks);
        for(Tuple<Long, Integer> block : rarestBlocks)
        {
            NetId node = schedule(block, nodeBlocks, workload);
            if (m.containsKey(node))
                m.get(node).add(block.fst());
            else
            {
                List<Long> blocks = new ArrayList<>();
                blocks.add(block.fst());
                m.put(node, blocks);
            }
            int tmp = workload.get(node);
            workload.put(node, tmp + 1);
        }
        return m;
    }

    private static NetId schedule (Tuple<Long, Integer> block, NodeBlocks nodeBlocks, Map<NetId, Integer> workload)
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
    private static void handle_get(String file) 
    {
        try 
        {
            // Send Repply
            trackerOutput.writeObject(new GetReqPacket(net_Id, file));
            trackerOutput.flush();

            //Get Response
            GetRepPacket resp = (GetRepPacket) trackerInput.readObject();

            Map<NetId, List<Long>> blockNode = scalonate (resp.get_nodeBlocks(), resp.get_nBlocks(), resp.getWorkLoad(), resp.getOwnedBlocks());

            for (Map.Entry<NetId, List<Long>> requests : blockNode.entrySet())
            {
                TransferPacket request = new GetFilesReq(TransferProtocol.TransferPacket.TypeMsg.GETF_REQ, net_Id, fbInfo.get_fileID(file), requests.getValue());
                InetAddress destinationAddress;
                try (DatagramSocket socket = new DatagramSocket()) {
                    byte[] sendData = request.serialize();
                    
                    destinationAddress = InetAddress.getByName(requests.getKey().getName() + Shared.Defines.DNS_Zone);

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destinationAddress, Shared.Defines.transferPort);

                    // Send the UDP packet
                    socket.send(sendPacket);

                    System.out.println("Packet sent successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break; //Debug
            }
            
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
        trackerOutput.writeObject(new RegReqPacket(net_Id, b));
        trackerOutput.flush();
        RegRepPacket rep= (RegRepPacket) trackerInput.readObject();
        b.set_FilesID(rep.get_fileId());
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
            register (fbInfo);

            Thread t = new Thread(new ConcurrentUDPServer(fbInfo, tc));
            t.start();

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
