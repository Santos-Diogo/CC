package Node;

import java.util.List;

import Shared.NetId;

class UDP_Job
{
    private NetId target;
    private String file;
    private List<Long> blocks;

    UDP_Job (NetId target, String file, List<Long> blocks)
    {
        this.target= target;
        this.file= file;
        this.blocks= blocks;
    }

    NetId getTarget ()
    {
        return this.target;
    }

    String getFile ()
    {
        return this.file;
    }

    List<Long> getBlocks ()
    {
        return this.blocks;
    }
}
