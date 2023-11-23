package Blocker;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BlockInfo
{
    private ReentrantReadWriteLock lock_rw;
    private Long nBlocks;               //Number of Blocks
    private List<Integer> filesBlocks;  //Which Blocks

    /**
     * 
     * @param nBlocks number of blocks the file has, -1 for unknown
     * @param filesBlocks list of blocks form file in the node
     */
    public BlockInfo (Long nBlocks, List<Integer> filesBlocks)
    {
        this.lock_rw= new ReentrantReadWriteLock();
        this.nBlocks= nBlocks;
        this.filesBlocks= filesBlocks;
    }

    public BlockInfo (BlockInfo b)
    {
        this.lock_rw= new ReentrantReadWriteLock();
        this.nBlocks= b.nBlocks;
        this.filesBlocks= b.filesBlocks;
    }

    public Long get_nBlocks ()
    {
        try
        {
            lock_rw.readLock().lock();
            return this.nBlocks;
        }
        finally
        {
            lock_rw.readLock().unlock();
        }
    }

    public List<Integer> get_filesBlocks () throws NullPointerException
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

    public void add_block (Integer block)
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
}