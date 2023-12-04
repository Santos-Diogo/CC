package Network.UDP.Packet;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;

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
    public long from;
    public long to;
    public static long pNnumber;
    private static long inc;

    public UDP_Packet (UDP_Packet.Type type, long from, long to)
    {
        this.type= type;
        this.from= from;
        this.to= to;
        this.pNnumber= inc;
        inc++;
    }

    public UDP_Packet (UDP_Packet p)
    {
        this.type= p.type;
        this.from= p.from;
        this.to= p.to;
        this.pNnumber= p.inc;
    }

    public byte[] serialize () throws Exception
    {
        ByteArrayOutputStream bs;
        ObjectOutputStream stream= new ObjectOutputStream(bs= new ByteArrayOutputStream());
        stream.writeObject(this);
        return bs.toByteArray();
    }
}
