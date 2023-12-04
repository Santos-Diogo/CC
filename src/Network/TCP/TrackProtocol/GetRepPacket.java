package Network.TCP.TrackProtocol;

import java.util.*;
import Shared.NetId;
import Shared.NodeBlocks;

/**
 * Temporary implementation
 */
public class GetRepPacket extends TrackPacket
{
    private long fileId;
    private Long nBlocks;
    private NodeBlocks nodeBlocks;
    private List<Long> ownedBlocks;
    private Map<NetId, Integer> workLoad;

    public GetRepPacket (NetId self, long fileId, Long nBlocks, NodeBlocks nodeBlocks, long from, long to, List<Long> ownedBlocks, Map<NetId, Integer> workload)
    {
        super (self, TypeMsg.GET_RESP, from, to);
        this.fileId= fileId;
        this.nBlocks= nBlocks;
        this.nodeBlocks= nodeBlocks;
        this.ownedBlocks = ownedBlocks;
        this.workLoad = workload;
    }

    public long get_fileId ()
    {
        return this.fileId;
    }

    public Long get_nBlocks ()
    {
        return this.nBlocks;
    }

    public NodeBlocks get_nodeBlocks()
    {
        return this.nodeBlocks;
    }

    public List<Long> getOwnedBlocks() {
        return ownedBlocks;
    }

    public Map<NetId, Integer> getWorkLoad() {
        return workLoad;
    }
}