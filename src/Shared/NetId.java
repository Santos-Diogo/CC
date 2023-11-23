package Shared;

import java.net.InetAddress;
import java.io.Serializable;

/**
 * This class is how the communicators identify each other
 */
public class NetId implements Serializable
{
    private InetAddress adr;

    public NetId (InetAddress adr)
    {
        this.adr= adr;
    }

    public InetAddress get_adr ()
    {
        return this.adr;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (this== obj)
            return true;
        if (obj== null || this.getClass()!= obj.getClass())
            return false;

        NetId n= (NetId) obj;

        if (!this.adr.equals(n.get_adr()))
            return false;

        return true;
    }
}
