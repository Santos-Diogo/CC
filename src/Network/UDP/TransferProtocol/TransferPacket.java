package Network.UDP.TransferProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class TransferPacket implements Serializable
{
    public enum TypeMsg
    {
        GET,    //Get a file Request
        TSF,    //Transfer packet
    };

    public TypeMsg type;                        //Message's type

    public TransferPacket (TransferPacket.TypeMsg type)
    {
        this.type= type;
    }

    /**
     * @return returns packet object written in bytes
     * @throws IOException failure in internall streams
     */
    public void serialize (DataOutputStream out) throws IOException
    {
        out.writeInt(this.type.ordinal());
    }

    public static TransferPacket deserialize (DataInputStream in) throws IOException
    {
        TypeMsg type = TypeMsg.values()[in.readInt()];
        return new TransferPacket(type);
    }
}
