import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class FSTracker
{
    private static ServerSocket tracker;

    public static void main(String[] args) throws ClassNotFoundException
    {
        int port = Integer.parseInt(args[0]);
        try 
        {
            tracker = new ServerSocket(port);
            System.out.println("Tracker ativo em 10.4.4.1, porta " + port);

            while (true)
            {
                Socket clientSocket = tracker.accept();
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                FSTrackerProtocol pacote = (FSTrackerProtocol) inputStream.readObject();
                
                new Thread (new Handle (pacote));
                //System.out.println(new String(pacote.getPayload(), StandardCharsets.UTF_8));
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