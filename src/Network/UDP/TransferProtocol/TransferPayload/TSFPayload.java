package Network.UDP.TransferProtocol.TransferPayload;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class TSFPayload implements TransferPayload
{
    public long blockNumber;
    public byte[] block;

     public TSFPayload (byte[] b) throws Exception
    {
        ObjectInputStream stream= new ObjectInputStream(new ByteArrayInputStream(b));
        TSFPayload p= (TSFPayload) stream.readObject();
        this.blockNumber= p.blockNumber;
        this.block= p.block;
    }
}