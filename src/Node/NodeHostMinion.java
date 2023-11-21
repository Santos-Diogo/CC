package Node;

import java.net.DatagramPacket;

public class NodeHostMinion implements Runnable
{
    private DatagramPacket p;

    public NodeHostMinion (DatagramPacket p)
    {
        this.p= p;
    }   
    
    public void run ()
    {
        try 
        {
            System.out.println("Mensagem recebida: " + new String(p.getData()));
            System.out.println("Endereço do remetente: " + p.getAddress());
            System.out.println("Porta do remetente: " + p.getPort());
            // Additional processing logic
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
