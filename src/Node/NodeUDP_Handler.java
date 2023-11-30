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
    private BlockingQueue<DatagramPacket> tasks;

    public NodeUDP_Handler (ThreadControl tc, BlockingQueue<DatagramPacket> tasks)
    {
        this.tc= tc;
        this.tasks= tasks;
    }

    
      //@TODO
    private void handleCON (TransferPacket packet)
    {
        return;
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
                    handleCON(transferP);
                    break;
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
                DatagramPacket packet = tasks.take();
                handle (packet);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }    
}
