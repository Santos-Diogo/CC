package Node;

import ThreadTools.ThreadControl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Network.TCP.TrackProtocol.GetRepPacket;
import Network.TCP.TrackProtocol.TrackPacket;
import Network.TCP.TrackProtocol.AddBlockPacket;
import Network.TCP.TrackProtocol.TrackPacket.TypeMsg;
import Network.UDP.Socket.SocketManager;
import Network.UDP.TransferProtocol.TransferPayload.TSFPayload;
import Shared.NetId;
import Shared.NodeBlocks;
import Shared.Tuple;

/**
 * Thread that handles transfer requests from the node
 */
public class TransferRequests implements Runnable
{
    NetId self;
    long selfId;
    ThreadControl tc;
    BlockingQueue<GetRepPacket> files;
    BlockingQueue<TrackPacket> tcpoutput;
    SocketManager udpManager;
    String dir;
    Map<NetId, PingUtil> pingInfo;
    DNScache dnscache;

    public TransferRequests (NetId self, long selfID, ThreadControl tc, BlockingQueue<GetRepPacket> files, BlockingQueue<TrackPacket> tcpoutput, SocketManager udpManager, String dir)
    {
        this.self = self;
        this.tc = tc;
        this.files = files;
        this.tcpoutput = tcpoutput;
        this.udpManager = udpManager;
        this.dir = dir;
        this.pingInfo = new HashMap<>();
        this.dnscache = new DNScache();
    }

    /**
     * Responsible for getting and assembling a file
     */
    private class FileGetter implements Runnable
    {
        GetRepPacket file;
        Network.UDP.Socket.SocketManager udpManager;
        //TCP socket Manager

        public FileGetter (GetRepPacket file, Network.UDP.Socket.SocketManager udpManager)
        {
            this.file= file;
            this.udpManager = udpManager;
        }

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
            // Só falta adicionar o que se vai usar para comparar, o resto já esta feito (relativo ao ping)
            nodes.sort(Comparator.comparingInt(workload :: get).thenComparing(node -> new Random().nextInt()));
            return nodes.get(0);
        }

        private static void appendFileContent(String filePath, BufferedWriter bw) throws IOException {
            try (FileReader fr = new FileReader(filePath);
                    BufferedReader br = new BufferedReader(fr)) {

                String line;
                // Read each line from the file and append it to the combined file
                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        }

        public static void concatenateFiles(List<String> filePaths, String outputFilePath) throws IOException {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
            for (String filePath : filePaths) {
                if (Files.exists(Path.of(filePath))) {
                    byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
                    outputStream.write(fileBytes);
                } else {
                    System.err.println("File not found: " + filePath);
                }
            }
        }
    }

        private static void deleteOriginalFiles(List<String> fileList) {
            // Iterate over the list of files and delete each file
            for (String filePath : fileList) {
                File file = new File(filePath);
                if (file.exists()) {
                    if (file.delete()) {
                    } else {
                    }
                } else {
                }
            }
        }

        public void run ()
        {
            
            NodeBlocks nodeblocks = file.get_nodeBlocks();
            long nBlocks = file.get_nBlocks();
            Map<NetId, Integer> workload = file.getWorkLoad();
            List<Long> ownedBlocks = file.getOwnedBlocks();
            Map<NetId, List<Long>> scalonated_blocks = scalonate(nodeblocks, nBlocks, workload, ownedBlocks);
            BlockingQueue<TSFPayload> queue = new LinkedBlockingQueue<>();
            try {
                for(Map.Entry<NetId, List<Long>> nodes : scalonated_blocks.entrySet())
                {
                    InetAddress node_Address;
                    NetId node = nodes.getKey();
                    if(dnscache.contains_NodeAdress(node))
                        node_Address = dnscache.get_AddressFromCache(node);
                    else
                    {
                        node_Address = InetAddress.getByName(node.getName() + Shared.Defines.DNS_Zone);
                        dnscache.add_AdressToCache(node, node_Address);
                    }
                    Thread t = new Thread(new Transfer(udpManager, node_Address, file.get_fileId(), nodes.getValue(), queue));
                    t.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            String newDir = dir + "/" + file.getName() + ".fsblk.";
            List<String> filePaths = new ArrayList<>();
            try{
                for(int i = 0; i< nBlocks; i++)
                {
                    TSFPayload block = queue.take();
                    System.out.println(block.blockNumber);
                    String newDirBlock = newDir + block.blockNumber;
                    filePaths.add(newDirBlock);
                    try (FileOutputStream fos = new FileOutputStream(newDirBlock)) {
                        System.out.println(new String (block.block));
                        fos.write(block.block);
                    }
                    AddBlockPacket upd = new AddBlockPacket(new TrackPacket(self, TypeMsg.ADD, selfId, 0), file.getName(), block.blockNumber);
                    tcpoutput.add(upd);
                }
                filePaths.sort(Comparator.comparingInt(s -> Character.digit(s.charAt(s.length() - 1), 10)));
                String wholefile = dir + "/" + file.getName();
                Thread.sleep(TimeUnit.SECONDS.toMillis(20));
                concatenateFiles(filePaths, wholefile);
                deleteOriginalFiles(filePaths);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }

    public void run()
    {
        Runnable pingCachedNodes = () -> {
            try{
                if (!dnscache.isEmpty())
                {
                    Set<NetId> cachedNodes = dnscache.getCachedNetIds();
                    for(NetId node : cachedNodes)
                    {
                        PingUtil results = new PingUtil(node.getName() + Shared.Defines.DNS_Zone);
                        this.pingInfo.put(node, results);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // Schedule the task to run every 30 seconds, with an initial delay of 0 seconds
        scheduler.scheduleAtFixedRate(pingCachedNodes, 0, 60, TimeUnit.SECONDS);

        
        while (tc.get_running())
        {
            try
            {
                //Take file to transfer
                GetRepPacket file= files.take();
                Thread t= new Thread(new FileGetter(file, udpManager));
                t.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
