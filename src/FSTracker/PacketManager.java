package FSTracker;

import FSProtocol.FSTrackerProtocol;
import java.util.*;


public class PacketManager 
{
    private Set<FSTrackerProtocol> p_set;

    PacketManager ()
    {
        p_set= new HashSet<FSTrackerProtocol>();
    }

    synchronized FSTrackerProtocol get_packet ()
    {
        Iterator<FSTrackerProtocol> i= p_set.iterator();
        if (i.hasNext())
        {
            FSTrackerProtocol p= (FSTrackerProtocol) i.next();
            i.remove();
            return p;
        }
        else
            return null;
    }

    synchronized boolean add_packet (FSTrackerProtocol p)
    {
        return p_set.add(p);
    }
}
