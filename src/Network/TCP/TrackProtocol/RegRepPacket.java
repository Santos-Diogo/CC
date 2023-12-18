package Network.TCP.TrackProtocol;

import java.util.Map;


public class RegRepPacket extends TrackPacket
{
    Map<String, Long> fileId;

    public RegRepPacket (TrackPacket track_packet, Map<String, Long> fileId)
    {
        super(track_packet);
        this.fileId= fileId;
    }

    public Map<String, Long> get_fileId ()
    {
        return this.fileId;
    }
}
