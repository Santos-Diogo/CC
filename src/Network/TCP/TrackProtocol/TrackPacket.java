package Network.TCP.TrackProtocol;

import java.io.Serializable;
import Shared.NetId;

public class TrackPacket implements Serializable 
{
    public enum TypeMsg 
    {
        REG_REQ,    // Register Request
        REG_REP,    // Register Response
        AVF_REQ,    // Available Files Request
        AVF_RESP,   // Available Files Response
        ADD_F,      // Add a File/ Blocks
        RM_F,       // Remove a File/ Blocks
        DC,         // Node disconnected from network
        GET_REQ,    // Get files request
        GET_RESP,   // Get files response
    }

    public NetId netId;
    public TypeMsg type;
    public long from;
    public long to;

    public TrackPacket(NetId n, TypeMsg type, long from, long to) 
    {
        this.netId= n;
        this.type= type;
    }
}