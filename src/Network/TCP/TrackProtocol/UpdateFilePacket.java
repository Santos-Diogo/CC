package Network.TCP.TrackProtocol;



public class UpdateFilePacket extends TrackPacket{
    
    private boolean add_rm; //true: add. false: rm.
    private long fileid;
    private long block;

    public UpdateFilePacket (TrackPacket track_packet, boolean add_rm, long fileid, Long block)
    {
        super(track_packet);
        this.add_rm = add_rm;
        this.fileid = fileid;
        this.block = block;
    }

    public boolean isAdd_rm() {
        return add_rm;
    }

    public long getFileid() {
        return fileid;
    }

    public Long getBlocks() {
        return block;
    }

    
}
