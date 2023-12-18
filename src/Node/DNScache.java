package Node;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import Shared.NetId;

public class DNScache {
    private Map<NetId, InetAddress> name_resolution;

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

    public boolean isEmpty ()
    {
        return name_resolution.isEmpty();
    }

    public Set<NetId> getCachedNetIds ()
    {
        return name_resolution.keySet();
    }
}
