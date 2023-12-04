package Network.UDP.Packet;

import java.io.Serializable;

public class UDP_Packet implements Serializable
{
    public enum Type 
    {
        CON,
        DC,
        MSG,
        ACK
    }
    public Type type;

    public UDP_Packet (UDP_Packet.Type type)
    {
        this.type= type;
    }
}
