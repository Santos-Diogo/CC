package Network.TCP.TrackProtocol;

import Shared.NetId;
import java.util.Map;

public class AvfRepPacket extends TrackPacket
{
    Map<String, Long> fileSizes;

    public AvfRepPacket(NetId n, Map<String, Long> files, long from, long to) 
    {
        super(n, TypeMsg.AVF_RESP, from, to);
        this.fileSizes = files;
    }

    public Map<String, Long> get_files() 
    {
        return this.fileSizes;
    }    
}
