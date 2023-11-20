package Track_Protocol;

import java.io.Serializable;
import Shared.Net_Id;

public class TrackPacket implements Serializable 
{
    public enum TypeMsg 
    {
        REG, // Register
        AVF_REQ, // Available Files Request
        AVF_RESP, // Available Files Response
        ADD_F, // Add a File/ Blocks
        RM_F, // Remove a File/ Blocks
        DC, // Node disconnected from network
        GET_REQ, // Get files request
        GET_RESP // Get files response
    }

    private Net_Id node;
    private TypeMsg type;

    public TrackPacket(Net_Id n, TypeMsg type) 
    {
        this.node= n;
        this.type= type;
    }

    public Net_Id getNode() 
    {
        return this.node;
    }

    public TypeMsg getType() {
        return type;
    }
}