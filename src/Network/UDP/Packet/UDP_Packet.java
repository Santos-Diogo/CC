package Network.UDP.Packet;

import java.io.Serializable;
import java.io.DataInputStream;
import java.io.DataOutputStream;


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

    public void serialize (DataOutputStream dos) throws Exception
    {
        dos.writeInt(type.ordinal());
        dos.writeLong(from);
        dos.writeLong(to);
        dos.writeLong(pNnumber);
    }

    public static UDP_Packet deserialize (DataInputStream dis) throws Exception
    {
        return new UDP_Packet(Type.values()[dis.readInt()], dis.readLong(), dis.readLong(), dis.readLong());
    }
}
