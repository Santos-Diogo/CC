package Network.TCP.TrackProtocol;

import Shared.NetId;

public class GetReqPacket extends TrackPacket
{
    private String file;

    public GetReqPacket (NetId n, String file)
    {
        super(n, TypeMsg.GET_REQ);
        this.file= file;
    }
    
    public String getFile ()
    {
        return this.file;
    }
}
