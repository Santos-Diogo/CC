package Network.TCP.TrackProtocol;

import java.util.Map;

import Shared.NetId;

public class RegRepPacket extends TrackPacket
{
    Map<String, Long> fileId;

    public RegRepPacket (NetId n, long from, long to, Map<String, Long> fileId)
    {
        super(n, TypeMsg.REG_REP, from, to);
        this.fileId= fileId;
    }

    public Map<String, Long> get_fileId ()
    {
        return this.fileId;
    }
}
