package FSProtocol;
import java.net.*;
import java.io.*;

public class FSTrackerProtocol implements Serializable 
{
    //Packages are immutable
    private final InetAddress srcIP; // Endereço IP de origem
    private final short type; // Tipo da mensagem
    private final byte[] payload;

    @Override
    public String toString ()
    {
        return "srcIP="+ srcIP+ " type="+ type; 
    }

    public FSTrackerProtocol(byte[] payload, short type, InetAddress srcIP)
    {
        this.payload = payload;
        this.type = type;
        this.srcIP = srcIP;
    }

    public InetAddress getSrcIP()
    {
        return srcIP;
    }

    public short getType()
    {
        return type;
    }

    public byte[] getPayload()
    {
        return payload;
    }
}
