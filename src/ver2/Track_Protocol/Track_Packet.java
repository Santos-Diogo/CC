package ver2.Track_Protocol;

import java.io.Serializable;
import java.net.InetAddress;

public class Track_Packet implements Serializable
{
    public enum TypeMsg
    {
        REG,
        ACK,
        AVF,
        GET
    }

    private final InetAddress src_ip;
    private final TypeMsg type;
    private final byte[] payload;
    
    public Track_Packet (InetAddress src_ip, TypeMsg type, byte[] payload)
    {
        this.src_ip= src_ip;
        this.type= type;
        this.payload= payload;
    }

    public InetAddress getSrc_ip() 
    {
        return src_ip;
    }

    public TypeMsg getType() 
    {
        return type;
    }

    public byte[] getPayload() 
    {
        return payload;
    }

    
}