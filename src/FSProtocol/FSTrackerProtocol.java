package FSProtocol;
import java.net.*;
import java.io.*;

public class FSTrackerProtocol implements Serializable 
{
    public enum TypeMsg
    {
        REG,
        ACK,
        AVF,
        GET
    };


    //Packages are immutable
    private final InetAddress srcIP; // Endereço IP de origem
    private final TypeMsg type ;
    private final byte[] payload;

    @Override
    public String toString ()
    {
        return "srcIP="+ srcIP+ " type="+ type; 
    }

    public FSTrackerProtocol(byte[] payload, TypeMsg type, InetAddress srcIP)
    {
        this.payload = payload;
        this.type = type;
        this.srcIP = srcIP;
    }

    public InetAddress getSrcIP()
    {
        return srcIP;
    }

    public TypeMsg getTypeMsg()
    {
        return type;
    }

    public byte[] getPayload()
    {
        return payload;
    }

}
