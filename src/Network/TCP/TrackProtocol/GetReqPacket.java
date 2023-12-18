package Network.TCP.TrackProtocol;


public class GetReqPacket extends TrackPacket
{
    private String file;

    public GetReqPacket (TrackPacket track_packet, String file)
    {
        super(track_packet);
        this.file= file;
    }
    
    public String getFile ()
    {
        return this.file;
    }
}
