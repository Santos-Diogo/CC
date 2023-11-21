package TrackProtocol;

import java.util.*;
import Shared.NetId;

/**
 * Temporary implementation
 */
public class GetRepPacket extends TrackPacket
{
    private int nBlocks;
    private Map<NetId,List<Integer>> nodeBlocks;

    public GetRepPacket (NetId self, int nBlocks, Map<NetId,List<Integer>> nodeBlocks)
    {
        super (self, TypeMsg.GET_RESP);
        this.nBlocks= nBlocks;
        this.nodeBlocks= nodeBlocks;
    }

    public int get_nBlocks ()
    {
        return this.nBlocks;
    }

    public Map<NetId,List<Integer>> get_nodeBlocks()
    {
        return this.nodeBlocks;
    }
}