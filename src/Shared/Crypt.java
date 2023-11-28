package Shared;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Class responsible for encrypting and decrypting the messages using the given key
 */
public class Crypt 
{
    private SecretKey sKey;

    public Crypt (SecretKey s)
    {
        this.sKey= s;
    }

    public byte[] encrypt(byte[] message) throws Exception 
    {
        //__Ciphering
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sKey);
        
        //Initialization vector (ensures different output for same text)
        byte[] iv = cipher.getIV();
        byte[] encryptedBytes = cipher.doFinal(message);
        
        // Combine IV and ciphertext
        byte[] result = new byte[encryptedBytes.length+ iv.length];
        System.arraycopy(encryptedBytes, 0, result, 0, encryptedBytes.length);
        System.arraycopy(iv, 0, result, encryptedBytes.length, iv.length);

        return result;
    }
    
    public byte[] decrypt(byte[] message) throws Exception 
    {   
        //__Decryption
        // Extract IV Assuming 128-bit
        byte[] iv = new byte[16]; 
        System.arraycopy(message, 0, iv, 0, iv.length);
    
        // Extract ciphertext
        byte[] ciphertext = new byte[message.length - iv.length];
        System.arraycopy(message, iv.length, ciphertext, 0, ciphertext.length);
    
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sKey, new IvParameterSpec(iv));
    
        return cipher.doFinal(ciphertext);
    }
}
