package TransferProtocol.TransferPayload;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class ACKPayload implements TransferPayload
{
    Long ackNumber;

    public ACKPayload (Long ackNumber)
    {
        this.ackNumber= ackNumber;
    }

    public TransferPayload deserialize (byte[] b) throws Exception
    {
        ObjectInputStream stream= new ObjectInputStream(new ByteArrayInputStream(b));
        ACKPayload p= (ACKPayload) stream.readObject();
        return p;
    }
}
