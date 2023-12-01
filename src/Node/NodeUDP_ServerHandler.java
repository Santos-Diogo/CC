package Node;

import Shared.*;
import ThreadTools.*;
import TransferProtocol.*;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;
import java.security.KeyPair;

public class NodeUDP_ServerHandler implements Runnable
{
    private ThreadControl tc;
    private SharedSocket sharedSocket;
    private BlockingQueue<DatagramPacket> tasks;
    private KeyPair myKeys;
    private Crypt crypt;

    NodeUDP_ServerHandler (ThreadControl tc, BlockingQueue<DatagramPacket> tasks, SharedSocket s)
    {
        this.tc= tc;
        this.tasks= tasks;
        this.sharedSocket= s;
    }
    
    //@TODO
    private void handleCON (TransferPacket packet) throws Exception
    {   
        //Get the crypt for connection from the recieved packet
        this.crypt= new Crypt(myKeys.getPrivate(), packet.getPayload());

        //Create packet with our own public key
        byte[] publicKey= myKeys.getPublic().getEncoded();
        TransferPacket p= new TransferPacket(TransferPacket.TypeMsg.CON, publicKey);

        //add bit check to the serialized packet
        byte[] pBytes= CRC.couple(p.serialize());

        sharedSocket.send(new DatagramPacket(pBytes, pBytes.length));

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
