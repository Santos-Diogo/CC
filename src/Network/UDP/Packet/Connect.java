package Network.UDP.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.PublicKey;

import Shared.Crypt;

public class Connect extends UDP_Packet
{
    public PublicKey publicKey;                 //PublicKey

    public Connect (UDP_Packet p, PublicKey publicKey)
    {
        super (p);
        this.publicKey= publicKey;
    }

    @Override
    public void serialize(DataOutputStream dos) throws Exception {
        super.serialize(dos);
        byte[] key = publicKey.getEncoded();
        dos.writeInt(key.length);
        dos.write(key);
    }

    public static Connect deserialize (DataInputStream dis, UDP_Packet packet) throws Exception 
    {
        int length = dis.readInt();
        byte[] enconded = new byte[length];
        dis.read(enconded, 0, length);
        PublicKey key = Crypt.deserializePublicKey(enconded);
        return new Connect(packet, key);
    }
}
