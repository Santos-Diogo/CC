package Node;

import Blocker.FileBlockInfo;
import ThreadTools.ThreadControl;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class NodeUDP_Server implements Runnable
{
    private FileBlockInfo fbInfo;                                           //Info about the node
    private ThreadControl tc;                                               //object that controlls NodeUDP_Server running status
    private byte[] buf;                                                     //byte buffer to use in the packets
    private KeyPair keyPair;                                                //private/ public key pair
    private Map<InetAddress, BlockingQueue<DatagramPacket>> handleQueue;    //Handler's Queue

    private static KeyPair generateKeyPair() throws Exception 
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
            this.buf= new byte[Shared.Defines.transferBuffer];
            this.keyPair= generateKeyPair();
            this.handleQueue= new HashMap<>();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Problem while starting UDP server");
        }
    }

    private void handlers (DatagramPacket packet)
    {
        InetAddress adr= packet.getAddress();
        BlockingQueue bq;

        if (!this.handleQueue.containsKey(adr))
        {
            this.handleQueue.put(adr, (bq= new BlockingQueue<DatagramPacket>()));
        }
        else
        {
            bq= this.handleQueue.get(adr);
        }

        bq.add(packet);
    }

    public void run ()
    {
        DatagramSocket socket= null;
        try
        {
            socket= new DatagramSocket (Shared.Defines.transferPort);
            while (tc.get_running())
            {
                //Recieve packet
                DatagramPacket packet= new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                handlers(packet);
                //Check if new connection and pass down to handler nodes
            }   
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Problem while handling UDP packets sent to own server.");
        }
        finally
        {
            socket.close();
        }
    }
}
