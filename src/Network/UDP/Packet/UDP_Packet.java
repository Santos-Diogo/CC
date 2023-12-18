package Network.UDP.Packet;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

/**
 * Packets handled by the UDP Manager.
 * Numbered for a connection and assigned specific "from" and "to" users.
 */
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

    public UDP_Packet (byte[] b) throws Exception
    {
        ObjectInputStream stream= new ObjectInputStream(new ByteArrayInputStream(b));
        UDP_Packet p= (UDP_Packet) stream.readObject();
        this.type= p.type;
        this.from= p.from;
        this.to= p.to;
        this.pNnumber= p.pNnumber;
    }

    public UDP_Packet (UDP_Packet.Type type, long from, long to, long pNumber)
    {
        this.type= type;
        this.from= from;
        this.to= to;
        this.pNnumber= pNumber;
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
