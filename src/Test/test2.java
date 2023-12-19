package Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Shared.CRC;

public class test2 {
    
    public static void main (String[] args)
    {
        try(DatagramSocket socket = new DatagramSocket(9090))
        {
            DatagramPacket p = new DatagramPacket(new byte[4000], 4000); 
            socket.receive(p);
            byte[] before = p.getData();
            System.out.println("Before decouple " + before);
            byte[] after = CRC.decouple(before);
            System.out.println("After decouple: " + after);
            String string = new String(after);
            System.out.println("After deserialize: " + string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
