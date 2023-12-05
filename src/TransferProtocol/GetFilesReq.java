package TransferProtocol;

import java.util.List;

import Shared.NetId;

public class GetFilesReq extends TransferPacket {

    public long fileid;
    public List<Long> blocks; 

    public GetFilesReq (TypeMsg type, NetId netid, long fileid, List<Long> blocks)
    {
        super(type, netid);
        this.fileid = fileid;
        this.blocks = blocks;
    }
    
    
}