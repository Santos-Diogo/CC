package Track_Protocol;

import Shared.Net_Id;

public class GetReqPacket extends TrackPacket
{
    private String file;

    public GetReqPacket (Net_Id n, String file)
    {
        super(n, TypeMsg.GET_REQ);
        this.file= file;
    }
    
    public String getFile ()
    {
        return this.file;
    }
}
