package Network.UDP.Packet;

import java.security.PublicKey;

public class Connect extends UDP_Packet
{
    public PublicKey publicKey;                 //PublicKey

    public Connect (UDP_Packet p, PublicKey publicKey)
    {
        super (p);
        this.publicKey= publicKey;
    }
}
