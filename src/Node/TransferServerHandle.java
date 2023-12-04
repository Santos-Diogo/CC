package Node;

import ThreadTools.ThreadControl;

import java.net.InetAddress;

import Network.UDP.Socket.SocketManager.IOQueue;
import Network.UDP.TransferProtocol.TransferPacket;
import Shared.Crypt;

import java.security.KeyPair;

public class TransferServerHandle implements Runnable
{
    private ThreadControl tc;
    private InetAddress target;
    private IOQueue ioQueue;
    private KeyPair keyPair;
    private Crypt crypt;

    public void connect (TransferPacket p) throws Exception
    {
        //Create crypt from first packet
        this.crypt= new Crypt(this.keyPair.getPrivate(), p.payload);


        
        //Send packet with own public key
        this.ioQueue.out.add(this.ioQueue.new OutPacket(this.target, new TransferPacket(this.keyPair.getPublic().getEncoded())));

        
    }

    TransferServerHandle (ThreadControl tc, TransferPacket p, Network.UDP.Socket.SocketManager manager, KeyPair keyPair)
    {
        try
        {
            this.tc= tc;
            this.target= p.source;
            this.ioQueue= manager.getQueue(manager.register());
            this.keyPair= keyPair;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run ()
    {
        while (this.tc.get_running())
        {
            //Fazer qualquer merdaa
        }
    }       
}
