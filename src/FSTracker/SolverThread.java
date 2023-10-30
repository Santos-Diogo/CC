package FSTracker;

import java.util.*;

import FSNode.FSNode;
import FSProtocol.FSTrackerProtocol;

/**
 * Class that defines the SolverThreads.<p>
 * This are threads created by the MainThread that actually handle the packets collected by the MainThread and put into the PacketManager
 * @author Diogo Santos
 */
public class SolverThread implements Runnable
{
    PacketManager pm;
    private volatile boolean stopRequested;

    SolverThread (PacketManager pm)
    {
        this.pm= pm;
        this.stopRequested= false;
    }

    synchronized FSTrackerProtocol get_packet ()
    {
        FSTrackerProtocol packet= pm.get_packet();

        if (packet== null)
            stop();
        return packet;
    }

    //Synchronized method that prevents multiple calls from different threads
    private synchronized void stop ()
    {
        stopRequested= true;
    }

    private static void REG ()
    {
        System.out.println("Mensagem de REG");
    }

    private static void ACK ()
    {
        System.out.println("Mensagem de ACK");
    }

    private static void AVF ()
    {
        System.out.println("Mensagem de AVF");
    }

    private static void GET ()
    {
        System.out.println("Mensagem de GET");
    }


    static void handle (FSTrackerProtocol packet)
    {
        if (packet== null)
            return;

        switch (packet.getTypeMsg())
        {
            case REG:
                REG();
                break;
            case ACK:
                ACK();
                break;
            case AVF:
                AVF();
                break;
            case GET:
                GET();
                break;
            default:
                System.out.println("Unknowkn type in the packet");
        }
    }



    public void run ()
    {
        while (!stopRequested)
        {
            handle (get_packet());
        }
    }    
}
