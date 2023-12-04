package Network.UDP.TransferProtocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;

public class TransferPacket implements Serializable
{
    public enum TypeMsg
    {
        CON,    //Connect to node
        GET,    //Get a file Request
        TSF,    //Transfer packet
        ACK     //Ack
    };

    public InetAddress source;                  //Source's Adress
    public TypeMsg type;                        //Message's type
    public long from;                           //From id
    public long to;                             //Destination id
    public byte[] payload;                      //Packet's payload (Encrypted)

    /**
     * @return returns packet object written in bytes
     * @throws Exception failure in internall streams
     */
    public byte[] serialize () throws Exception
    {
        ByteArrayOutputStream bs;
        ObjectOutputStream stream= new ObjectOutputStream(bs= new ByteArrayOutputStream());
        stream.writeObject(this);
        return bs.toByteArray();
    }

    public TransferPacket (byte[] b) throws Exception
    {
        ObjectInputStream stream= new ObjectInputStream(new ByteArrayInputStream(b));
        TransferPacket p= (TransferPacket) stream.readObject();
        this.type= p.type;
        this.from= p.from;
        this.to= p.to;
        this.payload= p.payload;
    }

    public TransferPacket (TransferPacket.TypeMsg type, long from, long to, byte[] payload)
    {
        this.type= type;
        this.from= from;
        this.to= to;
        this.payload= payload;
    }
}
