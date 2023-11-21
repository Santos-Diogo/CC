package Server;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Shared.NetId;

public class ServerInfo
{
    private class NodeBlock
    {
        private ReentrantReadWriteLock rwl;
        private NetId n;
        private List <Integer> blocks;

        /**
         * @param node_adr adress of the node
         * @param blocks list of blocks for the file we associate with in "file_node_blocks"
         */
        public NodeBlock (NetId n, List <Integer> blockss)
        {
            this.rwl= new ReentrantReadWriteLock();
            this.n= n;
	        this.blocks = null;
	        if(blockss != null)
	    	    this.blocks= new ArrayList<>(blockss);
        }

        /**
         * add a block to the node
         * @param block block to add
         */
        public void add_block (int block)
        {
            rwl.writeLock().lock();
            try
            {
                blocks.add(block);
            }
            finally
            {
                rwl.writeLock().unlock();
            }
        }
        
        /**
         * @return list of blocks 
         */
        public List <Integer> get_blocks ()
        {
            rwl.readLock().lock();
            try
            {
                return new ArrayList<>(this.blocks);
            }
            finally
            {
                rwl.readLock().unlock();
            }
        }
    }

    private ReentrantReadWriteLock rwl;
    private Map<String, Integer> fileSize;
    private Map<String, List<NodeBlock>> fileNodeBlocks;

    public ServerInfo ()
    {
        this.rwl= new ReentrantReadWriteLock();
        this.fileNodeBlocks= new HashMap <>();
    }

    /**
     * Add a file to be tracked
     * @param file file to be tracked
     * @param node node
     * @param blocks list of blocks in the file (null for full file)
     */
    public void add_file (String file, int size, NetId node, List<Integer> blocks)
    {
        List<NodeBlock> l;

        this.rwl.writeLock().lock();
        try
        {
            //if File not tracked
            if (!this.fileSize.containsKey(file))
            {
                //Put in fileSize
                this.fileSize.put(file, size);

                //Put in fileNodeBlocks
                l= new ArrayList<NodeBlock>();
                this.fileNodeBlocks.put(file, l);
            }
            else
            {
                //Find Node Block
                l= this.fileNodeBlocks.get(file);
            }
            
            //Add NodeBlock to the list
            l.add (new NodeBlock(node, blocks));
        }
        finally
        {
            this.rwl.writeLock().unlock();
        }
    }

    /**
     * @return Returns the names of the files currently stored
     */
    public List<String> get_files ()
    {
        return new ArrayList<>(this.fileNodeBlocks.keySet());
    }

    /**
     * Temporary method for transfering a file
     * @param file
     * @return
     */
    public NetId getTransfer (String file)
    {
        for (Map.Entry<String, List<NodeBlock>> e : this.fileNodeBlocks.entrySet())
        {
            if (e.getKey()== file)
            {
                return e.getValue().get(0).n;
            }
        }

        //Dind't find the file
        return null;
    }
}
