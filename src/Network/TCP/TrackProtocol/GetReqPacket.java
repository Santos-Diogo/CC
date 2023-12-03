package Network.TCP.TrackProtocol;

import Shared.NetId;

public class GetReqPacket extends TrackPacket
{
    private String file;

    public GetReqPacket (NetId n, long from, long to, String file)
    {
        super(n, TypeMsg.GET_REQ, from, to);
        this.file= file;
    }
    
    public String getFile ()
    {
        return this.file;
    }
}
