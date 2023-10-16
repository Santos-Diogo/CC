package FSTracker;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

import FSProtocol.FSTrackerProtocol;

public class FSTracker
{
    private static ServerSocket tracker;
    private static ThreadManager tm;

    public static void main(String[] args) throws ClassNotFoundException
    {
        int port = Integer.parseInt(args[0]);

        try 
        {
            tracker = new ServerSocket(port);
            tm= new ThreadManager();
            System.out.println("Tracker ativo em 10.4.4.1, porta " + port);

            while (true)
            {
                Socket clientSocket = tracker.accept();
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                FSTrackerProtocol pacote = (FSTrackerProtocol) inputStream.readObject();
                
                tm.l.lock();
                try
                {
                    if (tm.new_thread())
                        new Thread (new ThreadCode (pacote));
                    else
                        Handle.handle (pacote);
                }
                finally
                {
                    tm.l.unlock();
                }
                
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