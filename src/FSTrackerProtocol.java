import java.net.*;
import java.io.*;

public class FSTrackerProtocol implements Serializable 
{
    private byte[] payload;
    private short type; // Tipo da mensagem
    private InetAddress srcIP; // Endereço IP de origem

    
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
