package TransferProtocol;

import java.util.List;

import Shared.NetId;

public class GetFilesReq extends TransferPacket {

    public String file;
    public List<Long> blocks; 

    public GetFilesReq (TypeMsg type, NetId netid, String file, List<Long> blocks)
    {
        super(type, netid);
        this.file = file;
        this.blocks = blocks;
    }
    
    
}