package Shared;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class DNScache {
    Map<NetId, InetAddress> name_resolution;

    public DNScache ()
    {
        name_resolution = new HashMap<>();
    }

    public void add_AdressToCache (NetId node, InetAddress ip)
    {
        name_resolution.put(node, ip);
    }

    public InetAddress get_AddressFromCache (NetId node)
    {
        return name_resolution.get(node);
    }

    public boolean contains_NodeAdress (NetId node)
    {
        return name_resolution.containsKey(node);
    }
}
