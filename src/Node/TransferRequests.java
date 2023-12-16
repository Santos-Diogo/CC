package Node;

import ThreadTools.ThreadControl;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Shared.Defines;
import Shared.NetId;
import Shared.NodeBlocks;
import Shared.Tuple;

/**
 * Thread that handles transfer requests from the node
 */
class TransferRequests implements Runnable
{
    /**
     * Responsible for getting and assembling a file
     */
    private class FileGetter implements Runnable
    {
        long file;
        ThreadControl tc;
        Network.UDP.Socket.SocketManager udpManager;
        //TCP socket Manager

        FileGetter (long file, ThreadControl tc, Network.UDP.Socket.SocketManager udpManager)
        {
            this.file= file;
            this.tc= tc;
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

        public byte[] assembleFile (byte[][] blocks, long nBlocks)
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for(int i = 0; i < nBlocks; i++)
                outputStream.write(blocks[i], 0, blocks[i].length);
            return outputStream.toByteArray();
        }

        public void run ()
        {
            //Send escalonate Request to TrackerServer -> Requires TCP Socket
            NodeBlocks nodeblocks;
            long nBlocks;
            Map<NetId, Integer> workload;
            List<Long> ownedBlocks;
            Map<NetId, List<Long>> scalonated_blocks = scalonate(nodeblocks, nBlocks, workload, ownedBlocks);
            //Create threads for each node to get the packet -> Requeires UDP Socket
            
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

                    Thread t = new Thread(new Transfer(udpManager, node_Address, file, nodes.getValue()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[][] blocks;
            escrever_ficheiro(filename, assembleFile(blocks, nBlocks));
        }
    }

    ThreadControl tc;
    BlockingQueue<Long> files;
    Map<NetId, PingUtil> pingInfo;
    DNScache dnscache;

    public TransferRequests (ThreadControl tc, BlockingQueue<Long> files)
    {
        this.tc = tc;
        this.files = files;
        this.pingInfo = new HashMap<>();
        this.dnscache = new DNScache();
    }

    public void run()
    {
        Runnable pingCachedNodes = () -> {
            if (!dnscache.isEmpty())
            {
                Set<NetId> cachedNodes = dnscache.getCachedNetIds();
                for(NetId node : cachedNodes)
                {
                    PingUtil results = new PingUtil(node.getName() + Shared.Defines.DNS_Zone);
                    this.pingInfo.put(node, results);
                }
            }
        };

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // Schedule the task to run every 5 seconds, with an initial delay of 0 seconds
        scheduler.scheduleAtFixedRate(pingCachedNodes, 0, 30, TimeUnit.SECONDS);

        
        while (tc.get_running())
        {
            try
            {
                //Take file to transfer
                long file= files.take();
                Thread t= new Thread(new FileGetter (file, tc));
                t.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
