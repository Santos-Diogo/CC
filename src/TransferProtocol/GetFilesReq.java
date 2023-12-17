package TransferProtocol;

import java.io.DataOutputStream;
import java.io.IOException;
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
    
    public void serialize (DataOutputStream out) throws IOException
    {
        super.serialize(out);
        out.writeUTF(file);
        out.writeInt(blocks.size());
        for(Long l : blocks)
        {
            out.writeLong(l);
        }
    }
    
}