package Shared;

import java.net.InetAddress;

/**
 * This class is how the communicators identify each other
 */
public class Net_Id 
{
    private InetAddress adr;

    public Net_Id (InetAddress adr)
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

        Net_Id n= (Net_Id) obj;

        if (!this.adr.equals(n.get_adr()))
            return false;

        return true;
    }
}
