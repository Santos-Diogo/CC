package Network.UDP.TransferProtocol.TransferPayload;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class GETPayload implements TransferPayload
{
    public long file;
    public List<Long> blocks;

    public GETPayload (long file, List<Long> blocks)
    {
        this.file= file;
        this.blocks= blocks;
    }

    public GETPayload (byte[] b) throws Exception
    {
        ObjectInputStream stream= new ObjectInputStream(new ByteArrayInputStream(b));
        GETPayload p= (GETPayload) stream.readObject();
        this.file= p.file;
        this.blocks= p.blocks;
    }
}
