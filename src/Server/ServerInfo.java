package Server;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Blocker.BlockInfo;
import Shared.NetId;

public class ServerInfo 
{
    private class ServerBlockInfo extends BlockInfo 
    {
        NetId netId;

        public ServerBlockInfo(BlockInfo bInfo, NetId netId) {
            super(bInfo);
            this.netId = netId;
        }

        public NetId get_netId() {
            return this.netId;
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
        Long fileSize;
        long fileId;

        FileInfo() 
        {
            this.sbiList = new ArrayList<>();
            this.fileSize = null;
            this.fileId= id_inc++;
        }

        void set_fileSize(Long size) {
            if (size != null)
                this.fileSize = size;
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

        Long get_fileSize() {
            return this.fileSize;
        }

        long get_fileId ()
        {
            return this.fileId;
        }
    }

    ReentrantReadWriteLock rwl;
    Map<String, FileInfo> file_nodeData;

    public ServerInfo() 
    {
        this.rwl= new ReentrantReadWriteLock();
        this.file_nodeData = new HashMap<>();
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
            // Set FileSize
            f.set_fileSize(bInfo.get_nBlocks());

            //Return the file's id
            return f.fileId;
        }
        finally
        {
            rwl.writeLock().unlock();
        }
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
            return this.file_nodeData.get(file).fileSize;
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
    public Map<NetId, List<Long>> get_nodeInfoFile(String file) 
    {
        try
        {
            rwl.readLock().lock();
            List<ServerBlockInfo> l = this.file_nodeData.get(file).sbiList;
            Map<NetId, List<Long>> m = new HashMap<>();
            
            // We Map each Node to a NodeList
            for (ServerBlockInfo sbi : l) 
            {
                m.put(sbi.netId, sbi.get_filesBlocks());
            }
            return m;
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
    public Map<String, Long> get_filesWithSizes ()
    {
        try
        {
            rwl.readLock().lock();
            Map<String, Long> m= new HashMap<>();
            
            for (Map.Entry<String, FileInfo> e : this.file_nodeData.entrySet())
            {
                m.put(e.getKey(), e.getValue().fileSize);
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