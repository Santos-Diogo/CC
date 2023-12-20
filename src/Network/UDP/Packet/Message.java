package Network.UDP.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Message extends UDP_Packet
{
    public byte[] message;

    public Message (UDP_Packet p, byte[] message)
    {
        super(p);
        this.message= message;
    }

    
    public static Message deserialize (DataInputStream dis, UDP_Packet packet) throws Exception
    {
        int length = dis.readInt();
        byte[] message = new byte[length];
        dis.read(message, length, length);
        return new Message(packet, message);
    }

    @Override
    public void serialize (DataOutputStream dos) throws Exception
    {
        super.serialize(dos);
        dos.writeInt(message.length);
        dos.write(message);
    }
}
