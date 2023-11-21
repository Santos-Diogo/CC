package TrackProtocol;

import Shared.NetId;
import java.util.ArrayList;
import java.util.List;

public class AvfRepPacket extends TrackPacket
{
    List<String> files;

    public AvfRepPacket(NetId n, List<String> files) 
    {
        super(n, TypeMsg.AVF_RESP);
        this.files = new ArrayList<>(files);
    }

    public List<String> get_files() 
    {
        return new ArrayList<>(this.files);
    }    
}