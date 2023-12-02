package Node;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.annotation.Target;
import java.net.DatagramPacket;
import java.util.Map;
import java.net.InetAddress;

import ThreadTools.ThreadControl;
import Shared.NetId;

public class UDP_Server implements Runnable
{
    private ThreadControl tc;
    private BlockingQueue<DatagramPacket> packets;
    private Map<InetAddress, BlockingQueue<DatagramPacket>> handlerPackets;

    UDP_Server (ThreadControl tc, BlockingQueue<DatagramPacket> packets)
    {
        this.tc= tc;
        this.packets= packets;
    }

    public void run ()
    {
        while (tc.get_running())
        {
            try
            {
                DatagramPacket packet= packets.take();
                BlockingQueue<DatagramPacket> target;

                //Add Job to correct Thread's packet queue
                if (this.handlerPackets.containsKey(packet.getAddress()))
                {
                    this.handlerPackets.put(packet.getAddress(), target= new LinkedBlockingQueue<>());
                    Thread t= new Thread(new UDP_ServerHandler(tc, target));
                }
                else
                {
                    target= this.handlerPackets.get(packet.getTarget());
                }

                target.add(packet);
            }
            catch (Exception e)
            {   
                e.printStackTrace();
            }
        }
    }    
}
