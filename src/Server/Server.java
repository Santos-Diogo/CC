package Server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import ThreadTools.ThreadControl;

/**
 * Main server thread
 */
public class Server 
{
    private static ServerSocket socket;
    private static ThreadControl tc = new ThreadControl();
    private static ServerInfo serverInfo = new ServerInfo();
    private static InetAddress address;

    public static void main(String[] args) 
    {
        // Gets first connection from all nodes and then passes it down to a thread
        int port = Integer.parseInt(args[0]);
        try 
        {
            address = Inet4Address.getLocalHost();
            socket = new ServerSocket(port);
            System.out.println("Tracker ativo em " + address.getHostAddress() + ", porta " + port);

            while (true) 
            {
                Socket clientSocket = socket.accept();

                // The client socket is passed down to a thread.
                Thread t = new Thread(new ServerCom(clientSocket, tc, serverInfo));
                t.start();
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        finally 
        {
            // Close the ServerSocket when all is done
            if (socket != null && !socket.isClosed()) 
            {
                try 
                {
                    socket.close();
                    System.out.println("Job done");
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }

            // Terminate all threads using the ThreadControl object
            tc.set_running(false);
        }
    }
}
