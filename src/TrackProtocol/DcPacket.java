package TrackProtocol;

import Shared.NetId;

public class DcPacket extends TrackPacket
{
    public DcPacket (NetId n)
    {
        super (n, TypeMsg.DC);
    }
}
