package Network.TCP.TrackProtocol;



public class AddBlockPacket extends TrackPacket{

    private String file;
    private long block;

    public AddBlockPacket (TrackPacket track_packet, String file, Long block)
    {
        super(track_packet);
        this.file = file;
        this.block = block;
    }    

    public String getFile() {
        return file;
    }

    public Long getBlocks() {
        return block;
    }
    
}
