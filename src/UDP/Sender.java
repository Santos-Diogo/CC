package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import ThreadTools.ThreadControl;
import UDP.SocketManager;

public class Sender implements Runnable
{
    private DatagramSocket socket;
    private Map<InetAddress, SocketManager.UDP_Out> ipToOutput;
    private ThreadControl tc;

    /**
     * 
     * @param socket socket to use to send packets
     * @param ipToOutput maps a given target ip with information and packets to be sent
     * @param tc controlls the thread
     */
    Sender (DatagramSocket socket, Map<InetAddress, SocketManager.UDP_Out> ipToOutput, ThreadControl tc)
    {
        this.socket= socket;
        this.ipToOutput= ipToOutput;
        this.tc= tc;
    }

    public void run ()
    {
        while (tc.get_running())
        {
            //Iterate over all possible sending locations
            for (Map.Entry<InetAddress, SocketManager.UDP_Out> entry : ipToOutput.entrySet())
            {
                SocketManager.UDP_Out udpOut= entry.getValue();

                int n= udpOut.connectionInfo.getAvailableToTransfer();
                DatagramPacket p;

                //Send packets available to be sent corresponding to a given node
                for (; n> 0; n--)
                {
                    try
                    {
                        //Q is empty before capacity is elapsed
                        if ((p= udpOut.outPackets.peek())== null)
                        {
                            break;
                        }
                        this.socket.send(p);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                //Update available after consuming in the sending cycle
                udpOut.connectionInfo.setAvailableToTransfer(n);
            }
        }
    }
}
