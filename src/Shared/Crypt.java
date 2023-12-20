package Shared;

import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;

/**
 * Class responsible for encrypting and decrypting the messages using the given key
 */
public class Crypt 
{
    private SecretKey sKey;

    public Crypt(PrivateKey private_key, PublicKey public_key) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(private_key);
        keyAgreement.doPhase(public_key, true);
    
        // Ensure the agreed-upon key length is suitable for AES (128 bits)
        byte[] sharedSecret = keyAgreement.generateSecret();
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(sharedSecret);
    
        this.sKey = new SecretKeySpec(Arrays.copyOf(key, 16), "AES");
    }

    /**
     * Creates a keypair
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKeyPair () throws NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        // set keysize to 1024
        keyPairGenerator.initialize(1024);
        KeyPair pair = keyPairGenerator.generateKeyPair();
        return pair;
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
        ByteBuffer res_buffer= ByteBuffer.allocate(encryptedBytes.length+ iv.length);
        res_buffer.put(iv);
        res_buffer.put(encryptedBytes);
        byte[] result = res_buffer.array();

        return result;
    }
    
    public byte[] decrypt(byte[] message) throws Exception 
    {
        ByteBuffer message_buffer= ByteBuffer.wrap(message);

        // Extract IV Assuming 128-bit
        byte[] iv = new byte[16];
        message_buffer.get(iv, 0, iv.length);
        // Extract ciphertext
        byte[] ciphertext = new byte[message.length - iv.length];
        message_buffer.get(ciphertext);
    
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sKey, new IvParameterSpec(iv));

        return cipher.doFinal(ciphertext);
    }
    

    /**
     * Deserialize a PublicKey from a byte array
     * @param publicKeyBytes Byte array representing the serialized PublicKey
     * @return Deserialized PublicKey
     * @throws GeneralSecurityException
     */
    public static PublicKey deserializePublicKey(byte[] publicKeyBytes) throws GeneralSecurityException 
    {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
    }

    // Utility method to convert byte array to hex string for logging
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02X", b));
        }
        return hexStringBuilder.toString();
    }
}
