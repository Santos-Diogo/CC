package Network.UDP.Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;

public class Connect extends UDP_Packet
{
    public PublicKey publicKey;                 //PublicKey

    public Connect (UDP_Packet p, PublicKey publicKey)
    {
        super (p);
        this.publicKey= publicKey;
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream bs;
        ObjectOutputStream stream = new ObjectOutputStream(bs = new ByteArrayOutputStream());

        // Serialize the fields from the UDP_Packet class
        stream.writeObject(this);

        // Serialize the publicKey field
        stream.writeObject(publicKey);

        return bs.toByteArray();
    }

    public Connect (byte[] b) throws Exception 
    {
         super(b);

        // Deserialize the additional field(s) for Connect
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b));
        publicKey = (PublicKey) ois.readObject();
    }
}
