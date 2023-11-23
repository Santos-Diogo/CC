package TrackProtocol;

import java.io.Serializable;
import Shared.NetId;

public class TrackPacket implements Serializable 
{
    public enum TypeMsg 
    {
        REG,        // Register
        AVF_REQ,    // Available Files Request
        AVF_RESP,   // Available Files Response
        ADD_F,      // Add a File/ Blocks
        RM_F,       // Remove a File/ Blocks
        DC,         // Node disconnected from network
        GET_REQ,    // Get files request
        GET_RESP,   // Get files response
    }

    private NetId Net_Id;
    private TypeMsg type;

    public TrackPacket(NetId n, TypeMsg type) 
    {
        this.Net_Id= n;
        this.type= type;
    }

    public NetId getNet_Id() 
    {
        return this.Net_Id;
    }

    public TypeMsg getType() {
        return type;
    }
}