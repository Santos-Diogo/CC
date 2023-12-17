package TransferProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public TransferPacket (TransferPacket packet)
    {
        this.type= packet.type;
        this.netid= packet.netid;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.type.ordinal());
        this.netid.serialize(out);   
    }
    

    public static TransferPacket deserialize(DataInputStream in) throws IOException {
        TypeMsg type = TypeMsg.values()[in.readInt()];
        NetId netid = NetId.deserialize(in);
        return new TransferPacket(type, netid);
    }

}
