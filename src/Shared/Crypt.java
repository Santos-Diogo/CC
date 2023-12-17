package Shared;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;

/**
 * Class responsible for encrypting and decrypting the messages using the given key
 */
public class Crypt 
{
    private SecretKey sKey;

    /**
     * Creates a keypair
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKeyPair () throws NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        KeyPair pair = keyPairGenerator.generateKeyPair();
        return pair;
    }

    /**
     * Creates a secret key and constructs the Crypt
     * @param privateKey
     * @param publicKeyBytes
     * @throws Exception
     */
    public Crypt (PrivateKey private_key, PublicKey public_key) throws Exception
    {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(private_key);
        keyAgreement.doPhase(public_key, true);

        this.sKey= new SecretKeySpec(keyAgreement.generateSecret(), "AES");
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
