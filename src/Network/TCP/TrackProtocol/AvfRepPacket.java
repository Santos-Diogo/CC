package Network.TCP.TrackProtocol;

import java.util.Map;

public class AvfRepPacket extends TrackPacket
{
    Map<String, Long> fileSizes;

    public AvfRepPacket(TrackPacket track_packet, Map<String, Long> files) 
    {
        super(track_packet);
        this.fileSizes = files;
    }

    public Map<String, Long> get_files() 
    {
        return this.fileSizes;
    }    
}
