package Network.UDP.TransferProtocol.TransferPayload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import Network.UDP.TransferProtocol.TransferPacket;

public class GETPayload extends TransferPacket
{
    public long file;
    public List<Long> blocks;

    public GETPayload (long file, List<Long> blocks, TypeMsg type, byte[] payload)
    {
        super(type, payload);
        this.file= file;
        this.blocks= blocks;
    }

    public void serialize (DataOutputStream out) throws IOException
    {
        super.serialize(out);
        out.writeLong(file);
        out.writeInt(blocks.size());
        for(Long l : blocks)
            out.writeLong(l);
    }

    public static GETPayload deserialize (DataInputStream in, TransferPacket packet) throws IOException
    {
        long file = in.readLong();
        int size = in.readInt();
        List<Long> blocks = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
            blocks.add(in.readLong());
        return new GETPayload(file, blocks, packet.type, packet.payload);
    }
}
