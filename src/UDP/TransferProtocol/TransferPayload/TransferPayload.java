package UDP.TransferProtocol.TransferPayload;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public interface TransferPayload 
{
    default public byte[] serialize () throws Exception
    {
        ByteArrayOutputStream bs;
        ObjectOutputStream stream= new ObjectOutputStream(bs= new ByteArrayOutputStream());
        stream.writeObject(this);
        return bs.toByteArray();
    }

    public TransferPayload deserialize (byte[] b) throws Exception;
}
