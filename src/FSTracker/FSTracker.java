package FSTracker;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.*;

import FSProtocol.FSTrackerProtocol;

public class FSTracker
{
    private static ServerSocket tracker;
    private static ArrayList<Thread> t_list;
    private static Set<FSTrackerProtocol> p_list;
    private static int t_max;

    public static void main(String[] args) throws ClassNotFoundException
    {
        t_list= new ArrayList<Thread>();
        p_list= new HashSet<FSTrackerProtocol>();
        t_max= 8;

        int port = Integer.parseInt(args[0]);

        try 
        {
            tracker = new ServerSocket(port);
            System.out.println("Tracker ativo em 10.4.4.1, porta " + port);
    
            for (int i= 0; i< t_max; i++)
            {
                //creating t_max threads and "storing" them in the t_list ArrayList
                t_list.add(new Thread(new SolverThread(p_list)));
            }

            while (true)
            {
                Socket clientSocket = tracker.accept();
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

                //adding a packet to the packet list
                p_list.add ((FSTrackerProtocol) inputStream.readObject());
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            // Close the ServerSocket when all is done
            if (tracker != null && !tracker.isClosed())
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