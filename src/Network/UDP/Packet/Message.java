package Network.UDP.Packet;

public class Message extends UDP_Packet
{
    public long from;
    public long to;
    public byte[] message;

    Message (long from, long to, byte[] message)
    {
        super(Type.MSG);
        this.from= from;
        this.to= to;
        this.message= message;
    }    
}
