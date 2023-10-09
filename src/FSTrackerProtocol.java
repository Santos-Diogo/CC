import java.net.*;

public class FSTrackerProtocol {
    private byte[] payload;


    private short type; //Tipo da mensagem
    private InetAddress srcIP; //Endereço IP de origem

    public FSTrackerProtocol (byte[] payload, short type, InetAddress srcIP)
    {
        this.payload = payload;
        this.type = type;
        this.srcIP = srcIP;
    }
}
