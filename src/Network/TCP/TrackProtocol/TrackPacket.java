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
        ADD,        // Add a block to a node
        RM,         // Remove a block from a node
        WKL,        // Update the nodes workload
        DC,         // Node disconnected from network
        GET_REQ,    // Get files request
        GET_RESP,   // Get files response
    }

    public NetId netId;
    public TypeMsg type;
    public long from;
    public long to;

    /**
     * @param n target net id
     * @param type type of message
     * @param from from (tcp manager id)
     * @param to to (tcp manager id)
     */
    public TrackPacket (NetId n, TypeMsg type, long from, long to) 
    {
        this.netId= n;
        this.type= type;
        this.from= from;
        this.to= to;
    }

    public TrackPacket (TrackPacket p)
    {
        this.netId = p.netId;
        this.type= p.type;
        this.from= p.from;
        this.to= p.to;
    }
}