package Node;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.security.KeyPair;
import java.security.PublicKey;

import TransferProtocol.*;
import TransferProtocol.TransferPayload.CONPayload;
import TransferProtocol.TransferPayload.GETPayload;
import ThreadTools.ThreadControl;
import TrackProtocol.TrackPacket.TypeMsg;
import Shared.Crypt;

class TransferClientHandle implements Runnable
{
    private InetAddress target;                                             //Node to send packets to
    private BlockingQueue<TransferJob> jobs;                                //Job Input
    private BlockingQueue<TransferPacket> input;                            //Input from socket
    private BlockingQueue<DatagramPacket> output;                           //Output from socket
    private ThreadControl tc;                                               //Thread Controll
    private KeyPair keyPair;                                                //Own Security KeyPair
    private long sentMessage;                                               //Number of sent Message for Ack matching
    private long receivedMessage;                                           //Number of received Message for Ack matching
    private Crypt connectionCrypt;                                          //Key calculated in the connect proceedure

    TransferClientHandle (  InetAddress target,
                            BlockingQueue<TransferJob> jobs,
                            BlockingQueue<TransferPacket> input,
                            BlockingQueue<DatagramPacket> output,
                            ThreadControl tc,
                            KeyPair keyPair)
    {
        this.target= target;
        this.jobs= jobs;
        this.input= input;
        this.output= output;
        this.tc= tc;
        this.keyPair= keyPair;
        this.sentMessage= 0;
        this.receivedMessage= 0;
    }

    private void connect () throws Exception
    {
        //Send own public key
        CONPayload payload= new CONPayload(this.keyPair.getPublic());
        output.add(PacketWrapper.datagramMaker(target, Shared.Defines.transferPort, payload.serialize(), true, TransferPacket.TypeMsg.CON));

        //Get target's public key
        TransferPacket received= this.input.take();
        byte[] keyBytes= received.payload;

        //Produce SharedSecret via the Crypt
        this.connectionCrypt= new Crypt(this.keyPair.getPrivate(), keyBytes);
    }

    private void handle (TransferJob j) throws Exception
    {
        //Ask for blocks
        GETPayload payload= new GETPayload(j.file, j.blocks);
        byte[] encryptedPayload= connectionCrypt.encrypt(payload.serialize());

        //Add the packet created by the datagramMaker
        output.add(PacketWrapper.datagramMaker(target, Shared.Defines.transferPort, encryptedPayload, true, TransferPacket.TypeMsg.GET));

        //Recieve blocks
        
        
    }

    public void run ()
    {
        try
        {
            //Connect with target node
            connect ();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        while (this.tc.get_running())
        {
            try
            {
                handle(jobs.take());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        //Send disconnect message?
    }
}
