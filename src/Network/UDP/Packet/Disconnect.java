package Network.UDP.Packet;

public class Disconnect extends UDP_Packet
{
    Disconnect (long from, long target)
    {
        super (Type.DC);
    }
}
