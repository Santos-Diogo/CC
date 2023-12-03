package UDP.Socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ThreadTools.ThreadControl;

public class Sender implements Runnable
{
    private ReentrantReadWriteLock rwl;
    private DatagramSocket socket;
    private SocketManager manager;
    private Map<InetAddress, BlockingQueue<DatagramPacket>> outputQueue;    //Minions add their packets here "remotely"
    private Map<InetAddress, ConnectionInfo> connectionInfo;                //Should be accessed for consultation via some method that doesn't break Concurrence
    private ThreadControl tc;

    Sender (SocketManager manager, ThreadControl tc)
    {
        try
        {
            this.socket= new DatagramSocket(Shared.Defines.transferPort);
            this.manager= manager;
            this.outputQueue= new HashMap<>();
            this.connectionInfo= new HashMap<>();
            this.tc= tc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getSenderMinion ()
    {

    }

    public void run ()
    {
        while (tc.get_running())
        {
            try
            {
                this.rwl.readLock().lock();
                //Iterate over all possible sending locations

            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }
    }
}
