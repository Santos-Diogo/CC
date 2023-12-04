package Network.TCP.TrackProtocol;

import java.util.List;

import Shared.NetId;

public class UpdateFilePacket extends TrackPacket{
    
    private boolean add_rm; //true: add. false: rm.
    private long fileid;
    private List<Long> blocks;

    public UpdateFilePacket (NetId self, long from, long to, boolean add_rm, long fileid, List<Long> blocks)
    {
        super(self, TypeMsg.UPD, from, to);
        this.add_rm = add_rm;
        this.fileid = fileid;
        this.blocks = blocks;
    }

    public boolean isAdd_rm() {
        return add_rm;
    }

    public long getFileid() {
        return fileid;
    }

    public List<Long> getBlocks() {
        return blocks;
    }

    
}
