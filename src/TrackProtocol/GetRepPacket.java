package TrackProtocol;

import java.util.*;
import Shared.NetId;

/**
 * Temporary implementation
 */
public class GetRepPacket extends TrackPacket
{
    private Long nBlocks;
    private Map<NetId,List<Integer>> nodeBlocks;

    public GetRepPacket (NetId self, Long nBlocks, Map<NetId,List<Integer>> nodeBlocks)
    {
        super (self, TypeMsg.GET_RESP);
        this.nBlocks= nBlocks;
        this.nodeBlocks= nodeBlocks;
    }

    public Long get_nBlocks ()
    {
        return this.nBlocks;
    }

    public Map<NetId,List<Integer>> get_nodeBlocks()
    {
        return this.nodeBlocks;
    }
}