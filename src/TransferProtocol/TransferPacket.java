package TransferProtocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TransferPacket implements Serializable
{
    public enum TypeMsg
    {
        CON,    //Connect to node
        GET,    //Get a file Request
        TSF,    //Transfer packet
        ACK     //Ack
    };

    public TypeMsg type;                       //Message's type
    public boolean isFromClient;               //True if is from client. False if it is from server
    public byte[] payload;                     //Packet's payload (Encrypted)

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
        this.isFromClient= p.isFromClient;
        this.payload= p.payload;
    }

    public TransferPacket (TransferPacket.TypeMsg type, boolean isFromClient, byte[] payload)
    {
        this.type= type;
        this.isFromClient= isFromClient;
        this.payload= payload;
    }
}
