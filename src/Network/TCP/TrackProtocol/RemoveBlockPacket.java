package Network.TCP.TrackProtocol;



public class RemoveBlockPacket extends TrackPacket{

    private long fileid;
    private long block;

    public RemoveBlockPacket (TrackPacket track_packet, long fileid, Long block)
    {
        super(track_packet);
        this.fileid = fileid;
        this.block = block;
    }    

    public long getFileid() {
        return fileid;
    }

    public Long getBlocks() {
        return block;
    }
    
}
