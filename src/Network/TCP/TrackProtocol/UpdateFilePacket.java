package Network.TCP.TrackProtocol;

public class UpdateFilePacket extends TrackPacket{
    
    private boolean add_rm; //true: add. false: rm.
    private long fileid;
    private List<Long> blocks;


}
