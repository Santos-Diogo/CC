package UDP.Socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import Shared.CRC;
import ThreadTools.ThreadControl;
import UDP.Socket.SocketManager.IOQueue.OutPacket;

public class Sender implements Runnable
{
    private class Minion implements Runnable
    {
        private BlockingQueue<OutPacket> packets;                           //Packets to pass down to IP->Packet map in the Sender
        private ThreadControl tc;
        private Sender sender;

        Minion (Sender sender, BlockingQueue<OutPacket> packets)
        {
            this.packets= packets;
        }

        public void run ()
        {
            while (tc.get_running())
            {
                try
                {
                    //Take input packet
                    OutPacket packet= packets.take();
                
                    //Serialize packet and checksum it
                    byte[] serialized= packet.packet.serialize();
                    byte[] checksum= CRC.couple(serialized);    
                    
                    //Get correct Queue to insert Datagram
                    BlockingQueue<DatagramPacket> queue= sender.getPacketQueue(packet.destination);
                    if (queue== null)
                    {
                        queue= sender.newPacketQueue(packet.destination);
                    }

                    //Insert Ready to send Datagram into the correct Sender's Queue
                    queue.add(new DatagramPacket(checksum, checksum.length));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ConnectionInfo 
    {
        int availableToTransfer;

        public ConnectionInfo (int availableToTransfer)
        {
            this.availableToTransfer= availableToTransfer;
        }
    }

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

    /**
     * Initiate a sender thread to recieve new packets from "packets"
     * @param packets Queue to recieve packets from
     */
    public void getSenderMinion (BlockingQueue<OutPacket> packets)
    {
        Thread t= new Thread(new Minion(this, packets));
        t.start();
    }

    /**
     * Try to get an existant BlockingQueue
     * @param adr adress corresponding to the Queue
     * @return 
     */
    public BlockingQueue<DatagramPacket> getPacketQueue (InetAddress adr)
    {
        try
        {
            this.rwl.readLock().lock();
            return this.outputQueue.get(adr);
        }
        finally
        {
            this.rwl.readLock().unlock();
        }
    }

    /**
     * Get a new Queue after checking there isn't one already (Unsafe if we don't check 1st)
     * @param adr Adress for the queue
     * @return created queue
     */
    public BlockingQueue<DatagramPacket> newPacketQueue (InetAddress adr)
    {
        try
        {
            BlockingQueue<DatagramPacket> q= new LinkedBlockingQueue<>();
            
            this.rwl.writeLock().lock();
            this.outputQueue.put(adr, q);

            return q;
        }
        finally
        {
            this.rwl.writeLock().lock();
        }
    }

    /**
     * Increment Window
     * @param adr
     */
    public void incWindow (InetAddress adr)
    {
        
    }

    /**
     * Decrement Window
     * @param adr
     */
    public void decWindow (InetAddress adr)
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
                //Send all available to send packets
            }
            finally
            {
                this.rwl.readLock().unlock();
            }
        }
    }
}
