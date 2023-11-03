package ver2.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import ver2.SharedState.SharedState;
import ver2.ThreadTools.ThreadControl;

/**
 * Main server thread
 */
public class Server 
{
    private static ServerSocket socket;
    private static Set<Thread> threads;
    private static ThreadControl tc;
    private static SharedState ss;

    public static void main (String[] args)
    {
        //Gets first connection from all nodes and then passes it down to a thread
        int port= Integer.parseInt(args[0]);
        tc= new ThreadControl();

        try 
        {
            socket = new ServerSocket(port);
            System.out.println("Tracker ativo em 10.4.4.1, porta " + port);

            while (true)
            {
                Socket clientSocket = socket.accept();
                
                //The client socket is passed down to a thread.
                Thread t= new Thread(new ServerCom(clientSocket, tc, ss));
                threads.add(t);
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
            if (socket!= null && !socket.isClosed())
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
            tc.end();
        }
    }
}
