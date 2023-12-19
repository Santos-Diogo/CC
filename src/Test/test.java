package Test;

import Shared.CRC;
import Shared.Crypt;

import java.security.KeyPair;
import java.util.Arrays;

public class test 
{
    static boolean testCRC ()
    {
        String string= "test test test";
        String random= "random";

        byte[] serialized= string.getBytes();

        byte[] checked= CRC.couple(serialized);

        byte[] recovered= CRC.decouple(checked);

        byte[] other_random= random.getBytes();

        return Arrays.equals(serialized, recovered)&& (CRC.decouple(other_random)== null);
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
        boolean tests= testCRC()&& testEncrypt();
        System.out.println("Tests results: "+ tests);
    }
}
