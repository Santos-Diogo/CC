package Test;

import Shared.CRC;
import Shared.Crypt;

import java.security.KeyPair;
import java.util.Arrays;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class test 
{

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02X", b));
        }
        return hexStringBuilder.toString();
    }

    static void testCRC ()
    {
        try (DatagramSocket cenas = new DatagramSocket(9090)){
            String string= "teste teste teste";
            System.out.println("Before serialize: " + string);
            byte[] serialized= string.getBytes();
            System.out.println("Before couple " + serialized);
            System.out.println("Before couple bytesToHex " + bytesToHex(serialized));
            byte[] checked= CRC.couple(serialized);
            System.out.println("After couple " + checked);
            System.out.println("After couple bytesToHex " + bytesToHex(checked));
            DatagramPacket packet = new DatagramPacket(checked, checked.length,
            InetAddress.getByName("Portatil2.p2pcc") , 9090);
    
            cenas.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    static boolean testEncrypt ()
    {
        try
        {

            KeyPair key_a= Crypt.generateKeyPair();
            KeyPair key_b= Crypt.generateKeyPair();
            
            Crypt crypt_a= new Crypt(key_a.getPrivate(), key_b.getPublic());
            Crypt crypt_b= new Crypt(key_b.getPrivate(), key_a.getPublic());
            
            String message= "test test test";
            
            byte[] sent= message.getBytes();
            byte[] sent_encrypted= crypt_a.encrypt(sent);
            
            byte[] received_encrypted= sent_encrypted;
            byte[] received= crypt_b.decrypt(received_encrypted);
            
            return Arrays.equals(sent, received);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static void main (String[] args)
    {
        testCRC();
    }    
}
