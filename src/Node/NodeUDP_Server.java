package Node;

import Blocker.FileBlockInfo;
import ThreadTools.ThreadControl;
import Shared.*;
import TransferProtocol.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class NodeUDP_Server implements Runnable
{
    private FileBlockInfo fbInfo;
    private ThreadControl tc;
    private byte[] buf;
    private KeyPair keyPair;

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
            tc= this.tc;
            buf= new byte[Shared.Defines.transferBuffer];
            keyPair= generateKeyPair();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Problem while starting UDP server");
        }
    }

    //@TODO
    private void handleGet (TransferPacket packet)
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
                case GET:
                {
                    handleGet(transferP);
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
        DatagramSocket socket= null;
        try
        {
            socket= new DatagramSocket (Shared.Defines.transferPort);
            while (tc.get_running())
            {
                //Recieve packet
                DatagramPacket packet= new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                
                //Handle Packet
                handle (packet);
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
