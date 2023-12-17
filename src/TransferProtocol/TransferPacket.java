package TransferProtocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import Shared.NetId;

public class TransferPacket implements Serializable
{
    public enum TypeMsg
    {
        GETF_REQ,
        GETF_REP,
        ACK
    };

    public TypeMsg type;   //Message's type    
    public NetId netid;

    public TransferPacket (TypeMsg type, NetId netid)
    {
        this.type= type;
        this.netid= netid;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.type.ordinal());
        this.netid.serialize(out);   
    }
    

    public static TransferPacket deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis)) {

            return (TransferPacket) ois.readObject();
        }
    }

}
