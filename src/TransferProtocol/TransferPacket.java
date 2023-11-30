package TransferProtocol;

public class TransferPacket 
{
    public enum TypeMsg
    {
        CON,    //Connect to node
        GET
    };

    private TypeMsg type;   //Message's type    
    private byte[] payload; //Packet's payload

    public TransferPacket (TypeMsg type, byte[] payload)
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
