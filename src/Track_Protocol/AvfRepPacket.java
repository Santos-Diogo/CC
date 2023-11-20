package Track_Protocol;

import Shared.Net_Id;
import java.util.ArrayList;
import java.util.List;

public class AvfRepPacket extends TrackPacket
{
    List<String> files;

    public AvfRepPacket(Net_Id n, TypeMsg type_msg, List<String> files) 
    {
        super(n, type_msg);
        this.files = new ArrayList<>(files);
    }

    public List<String> get_files() 
    {
        return new ArrayList<>(this.files);
    }    
}
