package TransferProtocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TransferPacket implements Serializable
{
    public enum TypeMsg
    {
        CON,    //Connect to node
        GET
    };

    private TypeMsg type;   //Message's type    
    private byte[] payload; //Packet's payload

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

    public TransferPacket (TransferPacket.TypeMsg type, byte[] payload)
    {
        this.type= type;
        this.payload= payload;
    }

    public TypeMsg getType ()
    {
        return this.type;
    }

    public byte[] getPayload ()
    {
        return this.payload;
    }
}
