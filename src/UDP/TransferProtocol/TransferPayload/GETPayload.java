package UDP.TransferProtocol.TransferPayload;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class GETPayload implements TransferPayload
{
    long file;
    List<Long> blocks;

    public GETPayload (long file, List<Long> blocks)
    {
        this.file= file;
        this.blocks= blocks;
    }
    public TransferPayload deserialize (byte[] b) throws Exception
    {
        ObjectInputStream stream= new ObjectInputStream(new ByteArrayInputStream(b));
        GETPayload p= (GETPayload) stream.readObject();
        return p;
    }
}
