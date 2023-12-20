package Network.UDP.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Ack extends UDP_Packet
{
    public long ackNumber;

    public Ack (UDP_Packet p, long number)
    {
        super (p);
        this.ackNumber= number;
    }

    @Override
    public void serialize (DataOutputStream dos) throws Exception
    {
        super.serialize(dos);
        dos.writeLong(ackNumber);
    }

    public static Ack deserialize (DataInputStream dis, UDP_Packet packet) throws Exception
    {
        return new Ack(packet, dis.readLong());
    }
}
