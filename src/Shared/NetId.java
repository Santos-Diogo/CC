package Shared;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.Serializable;

/**
 * This class is how the communicators identify each other
 */
public class NetId implements Serializable
{
    private String name;

    public NetId (String name)
    {
        this.name= name;
    }

    public String getName ()
    {
        return this.name;
    }

    public InetAddress get_IP (String host)
    {
        try {
            return Inet4Address.getByName(host);
        } catch (UnknownHostException e) {
            System.err.println("Host não existe");
            return null;
        }
    }

    @Override
    public String toString() {
        return name;
    }
    @Override
    public boolean equals (Object obj)
    {
        if (this== obj)
            return true;
        if (obj== null || this.getClass()!= obj.getClass())
            return false;

        NetId n= (NetId) obj;

        if (!this.name.equals(n.getName()))
            return false;

        return true;
    }
}
