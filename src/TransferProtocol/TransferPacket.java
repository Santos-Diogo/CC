package TransferProtocol;

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
}
