package ver1.FSTracker;

import java.util.*;

import ver1.FSProtocol.FSTrackerProtocol;

/**
 * Class responsible for managing the "queue" of packages between the main and solver threads. Integrity is ensured by the function relative synchronization mechanisms
 * @author Diogo Santos
 */
public class PacketManager 
{
    private Set<FSTrackerProtocol> p_set;

    public PacketManager ()
    {
        p_set= new HashSet<FSTrackerProtocol>();
    }

    /**
     * @return A package and removes it from the set if there is one and returns null if there aren't any
     */
    public synchronized FSTrackerProtocol get_packet ()
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

    /**
     * Adds a packet to the set. Method to be used by the Main Thread
     * @param p     - packet to be added
     * @return      the result of the add operation to the set
     */
    public synchronized boolean add_packet (FSTrackerProtocol p)
    {
        return p_set.add(p);
    }
}
