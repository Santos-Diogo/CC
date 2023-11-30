package Node;

import Blocker.FileBlockInfo;
import ThreadTools.SharedSocket;
import ThreadTools.ThreadControl;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



public class NodeUDP_Server implements Runnable
{
    private FileBlockInfo fbInfo;                                           //Info about the node
    private ThreadControl tc;                                               //object that controlls NodeUDP_Server running status
    private KeyPair keyPair;                                                //private/ public key pair
    private Map<InetAddress, BlockingQueue<DatagramPacket>> handleQueue;    //Handler's Queue
    private SharedSocket sharedSocket;                                      //Socket manager class

    private KeyPair generateKeyPair() throws Exception 
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(Shared.Defines.securityKeySize);
        return keyPairGenerator.generateKeyPair();
    }

    public NodeUDP_Server (FileBlockInfo fbInfo, ThreadControl tc)
    {
        try
        {
            this.fbInfo= fbInfo;
            this.tc= tc;
            this.keyPair= generateKeyPair();
            this.handleQueue= new HashMap<>();
            this.sharedSocket= new SharedSocket(new DatagramSocket (Shared.Defines.transferPort));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Problem while starting UDP server");
        }
    }


    /**
     * Adds the packet to a corresponding queue handled by a given thread creted with that porpose
     * @param packet packet to handle
     */
    private void handlers (DatagramPacket packet)
    {
        InetAddress adr= packet.getAddress();
        BlockingQueue<DatagramPacket> bq;

        if (!this.handleQueue.containsKey(adr))
        {
            //Create queue
            bq= new LinkedBlockingQueue<>();
            this.handleQueue.put(adr, (bq));

            //Create thread to handle the queue sharing the same thread controll for cascading termination signaling
            Thread t= new Thread(new NodeUDP_Handler(this.tc, bq, sharedSocket));

            //Start the thread
            t.start();
        }
        else
        {
            //Store the already existent queue as bq.
            bq= this.handleQueue.get(adr);
        }

        //Add the packet to the assigned queue
        bq.add(packet);
    }

    public void run ()
    {
        try
        {
            while (tc.get_running())
            {
                //Create a new recieving packet with a new buffer
                DatagramPacket packet= new DatagramPacket(new byte[Shared.Defines.transferBuffer], Shared.Defines.transferBuffer);

                //Recieve packet
                this.sharedSocket.receive(packet);

                //"send" packet to handlers
                handlers(packet);
            }   
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Problem while handling UDP packets sent to own server.");
        }
        finally
        {
            this.sharedSocket.close();
        }
    }
}
