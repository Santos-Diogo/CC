package Test;

import Shared.CRC;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class test 
{
    static void testCRC ()
    {
        try (DatagramSocket cenas = new DatagramSocket(9090)){
            String string= "teste teste teste";
            System.out.println("Before serialize: " + string);
            byte[] serialized= string.getBytes();
            System.out.println("Before couple " + serialized);
            byte[] checked= CRC.couple(serialized);
            System.out.println("After decouple " + checked);
            DatagramPacket packet = new DatagramPacket(checked, checked.length,
            InetAddress.getByName("Portatil2.p2pcc") , 9090);
    
            cenas.send(packet);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main (String[] args)
    {
        testCRC();
    }    
}
