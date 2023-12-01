package Node;

import java.util.List;

import Shared.NetId;

class UDP_Job
{
    private NetId target;
    private int file;
    private List<Long> blocks;

    UDP_Job (NetId target, int file, List<Long> blocks)
    {
        this.target= target;
        this.file= file;
        this.blocks= blocks;
    }

    NetId getTarget ()
    {
        return this.target;
    }

    int getFile ()
    {
        return this.file;
    }

    List<Long> getBlocks ()
    {
        return this.blocks;
    }
}
