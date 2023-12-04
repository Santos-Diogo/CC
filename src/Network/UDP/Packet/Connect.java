package Network.UDP.Packet;

import java.security.PublicKey;

public class Connect extends UDP_Packet
{
    public PublicKey publicKey;                 //PublicKey

    public Connect (PublicKey publicKey)
    {
        super (Type.CON);
        this.publicKey= publicKey;
    }
}
