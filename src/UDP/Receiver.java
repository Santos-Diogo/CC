package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import Shared.CRC;
import ThreadTools.ThreadControl;
import TransferProtocol.TransferPacket;

public class Receiver implements Runnable
{
    private DatagramSocket s;
    private BlockingQueue<TransferPacket> inputPacketServer;                    //Packets recieved to give to server
    private BlockingQueue<TransferPacket> inputPacketClient;                    //Packets recieved to give to client
    private ThreadControl tc;

    Receiver (DatagramSocket s, BlockingQueue<TransferPacket> inputServer, BlockingQueue<TransferPacket> inputClient, ThreadControl tc)
    {
        this.s= s;
        this.inputPacketClient= inputClient;
        this.inputPacketServer= inputServer;
    }

    private void handle (DatagramPacket p)
    {
        byte[] crude= p.getData();
        byte[] checked= CRC.decouple(crude);

        //If the packet is valid
        if (checked!= null)
        {
            try
            {
                TransferPacket parsed= new TransferPacket(checked);

                //If a packet is from a client we send it to a server
                if (parsed.isFromClient)
                {
                    this.inputPacketServer.add(parsed);
                }
                //Reversed proceedure
                else
                {
                    this.inputPacketClient.add(parsed);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void run ()
    {
        while (this.tc.get_running())
        {
            try
            {
                DatagramPacket p= new DatagramPacket(new byte[Shared.Defines.transferBuffer], Shared.Defines.transferBuffer);
                this.s.receive(p);
                handle(p);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
