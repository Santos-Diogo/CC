package Network.UDP.Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Message extends UDP_Packet
{
    public byte[] message;

    public Message (UDP_Packet p, byte[] message)
    {
        super(p);
        this.message= message;
    }

    public Message (byte[] b) throws Exception
    {
        super(b);

        // Deserialize the additional field(s) for Connect
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b));
        message = (byte[]) ois.readObject();
    }

    public byte[] serialize () throws Exception
    {
        ByteArrayOutputStream bs;
        ObjectOutputStream stream= new ObjectOutputStream(bs= new ByteArrayOutputStream());
        stream.writeObject(this);
        return bs.toByteArray();
    }
}
