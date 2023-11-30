package Node;

import ThreadTools.ThreadControl;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;
import TransferProtocol.TransferPacket;

public class NodeUDP_Handler implements Runnable
{
    private ThreadControl tc;
    private BlockingQueue<TransferPacket> tasks;

    public NodeUDP_Handler (ThreadControl tc, BlockingQueue<TransferPacket> tasks)
    {
        this.tc= tc;
        this.tasks= tasks;
    }

    private void handleCon (TransferPacket packet) throws Exception
    {
        CONPacket p= (CONPacket) packet;
        
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
                case CON:
                    handleCon(transferP);
                    break;
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
        while (tc.get_running()) 
        {
            try
            {
                TransferPacket packet = tasks.take();
                handle (packet);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }    
}
