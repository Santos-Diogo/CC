package Network.TCP.TrackProtocol;

import java.util.*;
import Shared.NetId;

/**
 * Temporary implementation
 */
public class GetRepPacket extends TrackPacket
{
    private long fileId;
    private Long nBlocks;
    private Map<NetId,List<Long>> nodeBlocks;

    public GetRepPacket (NetId self, long fileId, Long nBlocks, Map<NetId,List<Long>> nodeBlocks, long from, long to)
    {
        super (self, TypeMsg.GET_RESP, from, to);
        this.fileId= fileId;
        this.nBlocks= nBlocks;
        this.nodeBlocks= nodeBlocks;
    }

    public long get_fileId ()
    {
        return this.fileId;
    }

    public Long get_nBlocks ()
    {
        return this.nBlocks;
    }

    public Map<NetId,List<Long>> get_nodeBlocks()
    {
        return this.nodeBlocks;
    }
}