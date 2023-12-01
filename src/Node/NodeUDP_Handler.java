package Node;

import Shared.*;
import ThreadTools.*;
import TransferProtocol.*;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.security.PublicKey;
import java.util.concurrent.BlockingQueue;
import java.security.KeyPair;

public class NodeUDP_Handler implements Runnable
{
    private ThreadControl tc;
    private SharedSocket sharedSocket;
    private BlockingQueue<DatagramPacket> tasks;
    private KeyPair myKeys;
    private Crypt crypt;

    public NodeUDP_Handler (ThreadControl tc, BlockingQueue<DatagramPacket> tasks, SharedSocket s)
    {
        this.tc= tc;
        this.tasks= tasks;
        this.sharedSocket= s;
    }
    
    //@TODO
    private void handleCON (TransferPacket packet) throws Exception
    {
        //Parse packet's payload as the other node's foreign key
        ObjectInputStream bytes= new ObjectInputStream(new ByteArrayInputStream(packet.getPayload()));
        PublicKey foreignKey= (PublicKey) bytes.readObject();
        
        //Send self's public key
        /* sharedSocket.send(new DatagramPacket(null, 0)); */

        //Get the crypt for connection
        this.crypt= new Crypt(myKeys.getPrivate(), foreignKey.getEncoded());
    }

    //@TODO
    private void handleGET (TransferPacket packet)
    {
        return;
    }

    private void handle (DatagramPacket packet) throws Exception
    {
        byte[] rawData= packet.getData();
        byte[] checkedData= Shared.CRC.decouple(rawData);

        //If the recieved data is intact
        if (checkedData!= null)
        {
            ObjectInputStream dataStream= new ObjectInputStream(new ByteArrayInputStream(checkedData));
            TransferPacket transferP= (TransferPacket) dataStream.readObject();

            switch (transferP.getType())
            {
                case CON:
                {
                    handleCON(transferP);
                    break;
                }
                case GET:
                {
                    handleGET(transferP);
                    break;
                }
                default:
                {
                    System.out.println("Fodeu-se");
                }
            }
        }
    }  

    public void run ()
    {
        while (tc.get_running()) 
        {
            try
            {
                DatagramPacket packet= tasks.take();
                handle (packet);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }    
}
