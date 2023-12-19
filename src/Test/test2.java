package Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Shared.CRC;

public class test2 {

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02X", b));
        }
        return hexStringBuilder.toString();
    }

    
    public static void main (String[] args)
    {
        try(DatagramSocket socket = new DatagramSocket(9090))
        {
            DatagramPacket p = new DatagramPacket(new byte[4000], 4000); 
            socket.receive(p);
            System.out.println("length " + p.getLength());
            int length = p.getLength();
            byte[] before = new byte[length];
            System.arraycopy(p.getData(), 0, before, 0, length);
            System.out.println("Before decouple " + before);
            System.out.println("Before decouple bytesToHex " + bytesToHex(before));
            byte[] after = CRC.decouple(before);
            System.out.println("After decouple: " + after);
            System.out.println("After decouple bytesToHex " + bytesToHex(after));
            String string = new String(after);
            System.out.println("After deserialize: " + string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
