import java.io.IOException;
import java.net.*;

public class FSTracker {

    private static ServerSocket tracker;
    public static void main (String[] args) 
    {
        int port = Integer.parseInt(args[0]);
        try {
            
            tracker = new ServerSocket(port);
            InetAddress localhost = InetAddress.getLocalHost();
            String ipv4 = localhost.getHostAddress();
            System.out.println("Tracker ativo em " + ipv4 + ", porta " + port);

            while (true)
            {
                Socket clientSocket = tracker.accept();
            } 

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the ServerSocket when all is done
            if (tracker != null && !tracker.isClosed()) {
                try {
                    tracker.close();
                    System.out.println("Server is closed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
}