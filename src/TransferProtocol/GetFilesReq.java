package TransferProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    public GetFilesReq (TransferPacket packet, String file, List<Long> blocks)
    {
        super(packet);
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

    public static GetFilesReq deserialize (DataInputStream in, TransferPacket packet) throws IOException
    {
        String file = in.readUTF();
        int size = in.readInt();
        List<Long> blocks = new ArrayList<>();
        for(int i = 0; i < size; i++)
            blocks.add(in.readLong());
        return new GetFilesReq(packet, file, blocks);
    }
    
}