package FSTracker;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.*;

import FSProtocol.FSTrackerProtocol;

/**
 * Class with the code belonging to the "MainThread"
 * @author  Diogo Santos
 * @author  Martim Félix
 * @author  Luís Ribeiro
 */
public class FSTracker
{
    private static ServerSocket tracker;
    private static ArrayList<Thread> t_list;
    private static PacketManager pm;
    private static int t_max;

    public static void main(String[] args) throws ClassNotFoundException
    {
        t_list= new ArrayList<Thread>();
        pm= new PacketManager();
        t_max= 8;

        int port = Integer.parseInt(args[0]);

        try 
        {
            tracker = new ServerSocket(port);
            System.out.println("Tracker ativo em 10.4.4.1, porta " + port);
    
            for (int i= 0; i< t_max; i++)
            {
                t_list.add(new Thread(new SolverThread(pm)));
            }

            while (true)
            {
                Socket clientSocket = tracker.accept();
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                pm.add_packet ((FSTrackerProtocol) inputStream.readObject());
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            // Close the ServerSocket when all is done
            if (tracker!= null && !tracker.isClosed())
            {
                try
                {
                    tracker.close();
                    System.out.println("Server is closed.");
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }

    }
}