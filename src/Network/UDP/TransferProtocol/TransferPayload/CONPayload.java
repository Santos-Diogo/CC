package Network.UDP.TransferProtocol.TransferPayload;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.PublicKey;

public class CONPayload implements TransferPayload
{
    private PublicKey key;

    public CONPayload (PublicKey key)
    {
        this.key= key;
    }

    public CONPayload (byte[] b) throws Exception
    {
        ObjectInputStream stream= new ObjectInputStream(new ByteArrayInputStream(b));
        CONPayload p= (CONPayload) stream.readObject();
        this.key= p.key;
    }
}
