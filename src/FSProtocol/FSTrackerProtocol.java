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

    private short type_translate (String str)
    {
        switch (str)
        {
            case "END":
                return 1;
            case "REG":
                return 2;
            //mais mensagens
            
            default:
                return 0;
        }
    }

    public FSTrackerProtocol(byte[] payload, String type, InetAddress srcIP)
    {
        this.payload = payload;
        this.type = type_translate(type);
        this.srcIP = srcIP;
    }

    public InetAddress getSrcIP()
    {
        return srcIP;
    }

    public byte[] getPayload()
    {
        return payload;
    }
}
