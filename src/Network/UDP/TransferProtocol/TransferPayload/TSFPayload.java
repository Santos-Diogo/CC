package Network.UDP.TransferProtocol.TransferPayload;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import Network.UDP.TransferProtocol.TransferPacket;

public class TSFPayload extends TransferPacket
{
    public long blockNumber;
    public byte[] block;

    public TSFPayload (long blockNumber, byte[] b, TypeMsg type)
    {
        super(type);
        this.blockNumber = blockNumber;
        this.block = b;
    }

    @Override
    public void serialize (DataOutputStream out) throws IOException
    {
        super.serialize(out);
        out.writeLong(blockNumber);
        out.writeInt(block.length);
        out.write(block);
    }

    public static TSFPayload deserialize (DataInputStream in, TransferPacket packet) throws IOException
    {
        long blockNumber = in.readLong();
        int size = in.readInt();
        byte[] block = new byte[size];
        in.read(block, 0, size);
        return new TSFPayload(blockNumber, block, packet.type);
    }
}