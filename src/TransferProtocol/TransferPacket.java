package TransferProtocol;

public class TransferPacket 
{
    public enum TypeMsg
    {
        GET
    };

    private TypeMsg type;

    public TransferPacket (TypeMsg type)
    {
        this.type= type;
    }

    public TypeMsg getType ()
    {
        return this.type;
    }
}
