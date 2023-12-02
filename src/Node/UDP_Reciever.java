package Node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

import Shared.CRC;
import ThreadTools.ThreadControl;

public class UDP_Reciever implements Runnable
{
    private DatagramSocket s;
    private BlockingQueue<DatagramPacket> inputPacketServer;                    //Packets recieved to give to server
    private BlockingQueue<DatagramPacket> inputPacketClient;                    //Packets recieved to give to client
    private ThreadControl tc;

    UDP_Reciever (DatagramSocket s, BlockingQueue<DatagramPacket> inputServer, BlockingQueue<DatagramPacket> inputClient, ThreadControl tc)
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
