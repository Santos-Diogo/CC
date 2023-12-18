package Network.UDP.TransferProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class TransferPacket implements Serializable
{
    public enum TypeMsg
    {
        CON,    //Connect to node
        GET,    //Get a file Request
        TSF,    //Transfer packet
        ACK     //Ack
    };

    public TypeMsg type;                        //Message's type
    public byte[] payload;                      //Packet's payload (Encrypted)

    public TransferPacket (TransferPacket.TypeMsg type, byte[] payload)
    {
        this.type= type;
        this.payload= payload;
    }

    /**
     * @return returns packet object written in bytes
     * @throws IOException failure in internall streams
     */
    public void serialize (DataOutputStream out) throws IOException
    {
        out.writeInt(this.type.ordinal());
        out.writeInt(payload.length);
        out.write(payload);
    
    }

    public static TransferPacket deserialize (DataInputStream in) throws IOException
    {
        TypeMsg type = TypeMsg.values()[in.readInt()];
        int size = in.readInt();
        byte[] payload = new byte[size];
        in.read(payload, 0, size);
        return new TransferPacket(type, payload);

    }
}
