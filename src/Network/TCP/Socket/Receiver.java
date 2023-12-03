package Network.TCP.Socket;

public class Receiver implements Runnable
{
    private DatagramSocket socket;
    private SocketManager manager;
    private ThreadControl tc;

    Receiver (SocketManager manager, ThreadControl tc)
    {
        try
        {
            this.socket= new DatagramSocket(Shared.Defines.transferPort);
            this.manager= manager;
            this.tc= tc;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

                //Send package to correspondig ID
                IOQueue q= this.manager.getQueue(parsed.to);
                q.in.add(parsed);
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
                this.socket.receive(p);
                handle(p);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

