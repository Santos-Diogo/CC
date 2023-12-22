package Server;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import Blocker.BlockInfo;
import Shared.NetId;
import Shared.NodeBlocks;

public class ServerInfo 
{
    private class ServerBlockInfo extends BlockInfo 
    {
        NetId netId;

        public ServerBlockInfo(BlockInfo bInfo, NetId netId) {
            super(bInfo.get_filesBlocks());
            this.netId = netId;
        }

        public boolean contains_NetID(NetId node) {
            return this.netId.equals(node);
        }
    }

    /**
     * Has to be concurrency ready
     */
    private class FileInfo 
    {
        static long id_inc= 0;
        List<ServerBlockInfo> sbiList;
        long fileId;

        FileInfo() 
        {
            this.sbiList = new ArrayList<>();
            this.fileId= id_inc++;
        }

        void add_list(ServerBlockInfo sbi) {
            this.sbiList.add(sbi);
        }

        /**
         * Removes a ServerBlockInfo with a given netId
         * 
         * @param id
         */
        void rm_list(NetId id) {
            this.sbiList.removeIf(element -> element.contains_NetID(id));
        }

        boolean isEmpty() {
            return this.sbiList.isEmpty();
        }

        boolean isAvailable (Long nBlocks) {
            Set<Long> available_blocks = new HashSet<>();
            for(ServerBlockInfo sbi : sbiList) 
            {
                List<Long> blocks = sbi.get_filesBlocks();
                if (blocks == null)
                    return true;
                available_blocks.addAll(blocks);
            }
            if (available_blocks.size() == nBlocks)
                return true;
            return false;
        }

        boolean nodeHasFile(NetId node) {
            for(ServerBlockInfo sbi : sbiList)
                if(sbi.contains_NetID(node) && sbi.has_entireFile())
                    return true;
            return false;
        }
    }

    ReentrantReadWriteLock rwl;
    Map<String, FileInfo> file_nodeData;
    FileHistory fileHistory;
    Map<NetId, Integer> node_load;

    public ServerInfo() 
    {
        this.rwl= new ReentrantReadWriteLock();
        this.file_nodeData = new HashMap<>();
        this.fileHistory = new FileHistory();
        this.node_load = new HashMap<>();
    }

    /**
     * @param fileName
     * @param netId
     * @param bInfo
     * @return Returns the inserted file's corresponding Id
     */
    public long add_file(String fileName, NetId netId, BlockInfo bInfo) 
    {
        try
        {
            rwl.writeLock().lock();
            FileInfo f;

            // No file with that name exists
            if (!this.file_nodeData.containsKey(fileName)) {
                this.file_nodeData.put(fileName, f = new FileInfo());
            }
            // Get the list to add that entry
            else {
                f = this.file_nodeData.get(fileName);
            }
            
            // Add info from node about file
            f.add_list(new ServerBlockInfo(bInfo, netId));
        
            //Return the file's id
            return f.fileId;
        }
        finally
        {
            rwl.writeLock().unlock();
        }
    }

    /**
     * This function is called with extra argument 'filesize'. 
     * It is used when a complete file is detected in a node's shared directory
     * 
     * @param fileName
     * @param netId
     * @param bInfo
     * @param filesize 
     * @return Returns the inserted file's corresponding Id
     */
    public long add_file(String fileName, NetId netId, BlockInfo bInfo, Long filesize) 
    {
        try
        {
            rwl.writeLock().lock();
            FileInfo f;

            // No file with that name exists
            if (!this.file_nodeData.containsKey(fileName)) {
                this.file_nodeData.put(fileName, f = new FileInfo());
            }
            // Get the list to add that entry
            else {
                f = this.file_nodeData.get(fileName);
            }
            
            // Add info from node about file
            f.add_list(new ServerBlockInfo(bInfo, netId));
            
            //
            fileHistory.add_fileHistory(fileName, filesize);

            //Return the file's id
            return f.fileId;
        }
        finally
        {
            rwl.writeLock().unlock();
        }
    }


    public void addTo_fileHistory (String file, Long filesize)
    {
        rwl.writeLock().lock();
        try {
            fileHistory.add_fileHistory(file, filesize);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public boolean nodeHasFile(NetId node, String file)
    {
        return file_nodeData.get(file).nodeHasFile(node);
    }

    public void add_load (Map<NetId, Integer> load)
    {
        rwl.writeLock().lock();
        try{
            for(Map.Entry<NetId, Integer> e : load.entrySet())
            {   
                NetId node = e.getKey();
                int old_value = this.node_load.get(node);
                this.node_load.put(node, old_value + e.getValue());
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public void rm_load (NetId node)
    {
        rwl.writeLock().lock();
        try{
            int old_value = this.node_load.get(node);
            this.node_load.put(node, old_value - 1);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    /**
     * 
     * @param node Node to be registred in the load Map
     */
    public void register_inLoad (NetId node)
    {
        rwl.writeLock().lock();
        try{
            this.node_load.put(node, 0);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    public Map<NetId, Integer> get_workLoad (Set<NetId> nodes)
    {
        return this.node_load.entrySet()
            .stream()
            .filter((entry) -> nodes.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry :: getKey, Map.Entry :: getValue));
    }

    public List<String> get_files() 
    {
        try
        {
            rwl.readLock().lock();
            return this.file_nodeData.keySet().stream().toList();
        }
        finally
        {
            rwl.readLock().unlock();
        }
    }

    public Long get_nBlocks(String file) 
    {
        try
        {
            rwl.readLock().lock();
            return this.fileHistory.get_fileNBlocks(file);
        }
        finally
        {
            rwl.readLock().unlock();
        }
    }

    /**
     * Returns a map that maps nodes to the blocks they own of a given file
     * 
     * @param file
     * @return
     */
    public NodeBlocks get_nodeInfoFile(String file, NetId requesting_node, List<Long> ownedBlocks) 
    {
        try
        {
            rwl.readLock().lock();
            List<ServerBlockInfo> l = this.file_nodeData.get(file).sbiList;
            Map<NetId, List<Long>> m = new HashMap<>();
            
            // We Map each Node to a NodeList
            for (ServerBlockInfo sbi : l) 
            {
                if(sbi.contains_NetID(requesting_node))
                {
                    ownedBlocks.addAll(sbi.get_filesBlocks());
                    continue;
                } 
                m.put(sbi.netId, sbi.get_filesBlocks());
            }
            return new NodeBlocks(m);
        }
        finally
        {
            rwl.readLock().unlock();
        }
    }

    public void remove_infoFromNode(NetId node) 
    {
        try
        {
            rwl.writeLock().lock();
            Iterator<Map.Entry<String, FileInfo>> iterator = this.file_nodeData.entrySet().iterator();
            
            while (iterator.hasNext()) {
                Map.Entry<String, FileInfo> entry = iterator.next();
                FileInfo f = entry.getValue();
                
                f.rm_list(node);
                
                if (f.isEmpty()) {
                    iterator.remove(); // Remove the current entry using the iterator
                }
            }
        }
        finally
        {
            rwl.writeLock().unlock();
        }
    }

    /**
     * @return FileSizes mapped to corresponding filenames
     */
    public Map<String, Long> get_filesWithSizes (NetId requesting_node)
    {
        try
        {
            rwl.readLock().lock();
            Map<String, Long> m= new HashMap<>();
            
            for (Map.Entry<String, FileInfo> e : this.file_nodeData.entrySet())
            {
                FileInfo file = e.getValue();
                String fileName = e.getKey();
                if(fileHistory.contains_file(fileName) && file.isAvailable(fileHistory.get_fileNBlocks(fileName)) && !file.nodeHasFile(requesting_node))
                    m.put(fileName, fileHistory.get_fileNBlocks(fileName));
            }
            
            return m;
        }
        finally
        {
            rwl.readLock().unlock();
        }
    }

    public long get_fileId (String file)
    {
        try
        {
            rwl.readLock().lock();
            return this.file_nodeData.get(file).fileId;
        }
        finally
        {
            rwl.readLock().unlock();
        }
    }
}
