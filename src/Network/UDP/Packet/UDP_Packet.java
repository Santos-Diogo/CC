package Network.UDP.Packet;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

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
    public long pNnumber;
    private static long inc;

    public UDP_Packet (byte[] b) throws Exception
    {
        ObjectInputStream stream= new ObjectInputStream(new ByteArrayInputStream(b));
        UDP_Packet p= (UDP_Packet) stream.readObject();
        this.type= p.type;
        this.from= p.from;
        this.to= p.to;
        this.pNnumber= p.pNnumber;
    }

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
        this.pNnumber= p.pNnumber;
    }

    public byte[] serialize () throws Exception
    {
        ByteArrayOutputStream bs;
        ObjectOutputStream stream= new ObjectOutputStream(bs= new ByteArrayOutputStream());
        stream.writeObject(this);
        return bs.toByteArray();
    }
}
