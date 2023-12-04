package Network.UDP.Packet;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class Message extends UDP_Packet
{
    public long from;
    public long to;
    public byte[] message;

    public Message (long from, long to, byte[] message)
    {
        super(Type.MSG);
        this.from= from;
        this.to= to;
        this.message= message;
    }

    public byte[] serialize () throws Exception
    {
        ByteArrayOutputStream bs;
        ObjectOutputStream stream= new ObjectOutputStream(bs= new ByteArrayOutputStream());
        stream.writeObject(this);
        return bs.toByteArray();
    }
}
