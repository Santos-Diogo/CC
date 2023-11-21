package Block;

import java.util.List;

public class BlockInfo
{
    private Long nBlocks;               //Number of Blocks
    private List<Integer> filesBlocks;  //Which Blocks

    /**
     * 
     * @param nBlocks number of blocks the file has, -1 for unknown
     * @param filesBlocks list of blocks form file in the node
     */
    public BlockInfo (Long nBlocks, List<Integer> filesBlocks)
    {
        this.nBlocks= nBlocks;
        this.filesBlocks= filesBlocks;
    }

    public Long get_nBlocks ()
    {
        return this.nBlocks;
    }

    public List<Integer> get_filesBlocks () throws NullPointerException
    {
        return this.filesBlocks;
    }

    public void add_block (Integer block)
    {
        filesBlocks.add(block);
    }
}