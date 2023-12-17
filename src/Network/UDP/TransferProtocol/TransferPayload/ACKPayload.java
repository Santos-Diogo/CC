package Network.UDP.TransferProtocol.TransferPayload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import Network.UDP.TransferProtocol.TransferPacket;

public class ACKPayload extends TransferPacket
{
    Long ackNumber;

    public ACKPayload (Long ackNumber, TypeMsg type, byte[] payload)
    {
        super(type, payload);
        this.ackNumber= ackNumber;
    }

    public void serialize (DataOutputStream out) throws IOException
    {
        super.serialize(out);
        out.writeLong(ackNumber);
    }

    public static ACKPayload deserialize (DataInputStream in, TransferPacket packet) throws IOException
    {
        long ackNumber = in.readLong();
        return new ACKPayload(ackNumber, packet.type, packet.payload);
    }
}
