package Network.UDP.Packet;

public class Ack extends UDP_Packet
{
    public long ackNumber;

    public Ack (UDP_Packet p, long number)
    {
        super (p);
        this.ackNumber= number;
    }
}
