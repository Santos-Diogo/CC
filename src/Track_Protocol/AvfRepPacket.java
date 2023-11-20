package Track_Protocol;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class AvfRepPacket extends TrackPacket
{
    List<String> files;

    public AvfRepPacket(Inet4Address src_ip, TypeMsg type_msg, List<String> files) 
    {
        super(src_ip, type_msg);
        this.files = new ArrayList<>(files);
    }

    public List<String> get_files() 
    {
        return new ArrayList<>(this.files);
    }    
}
