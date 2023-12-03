package Blocker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.Serializable;

public class BlockInfo implements Serializable
{
    private ReentrantReadWriteLock lock_rw;
    private List<Long> filesBlocks;  //Which Blocks

    public BlockInfo ()
    {
        this.lock_rw= new ReentrantReadWriteLock();
        this.filesBlocks= new ArrayList<>();
    }
    
    /**
     * 
     * @param filesBlocks list of blocks form file in the node, null if it has all the blocks
     */
    public BlockInfo (List<Long> filesBlocks)
    {
        this.lock_rw= new ReentrantReadWriteLock();
        this.filesBlocks= filesBlocks;
    }

    public List<Long> get_filesBlocks () throws NullPointerException
    {
        try
        {
            lock_rw.readLock().lock();
            return this.filesBlocks;
        }
        finally
        {
            lock_rw.readLock().unlock();
        }
    }

    public void add_block (Long block)
    {
        try
        {
            lock_rw.writeLock().lock();
            filesBlocks.add(block);
        }
        finally
        {
            lock_rw.writeLock().unlock();
        }
    }

    public boolean has_entireFile ()
    {
        return (this.filesBlocks == null) ? true : false;
    }
}
