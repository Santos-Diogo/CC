package Network.UDP.Packet;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class Message extends UDP_Packet
{
    public byte[] message;

    public Message (UDP_Packet p, byte[] message)
    {
        super(p);
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
