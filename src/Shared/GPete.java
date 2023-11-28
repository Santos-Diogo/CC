package Shared;

import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.MessageDigest;

public class GPete 
{

    public static void main(String[] args) throws Exception 
    {
        // Alice and Bob generate their key pairs
        KeyPair aliceKeyPair = generateKeyPair();
        KeyPair bobKeyPair = generateKeyPair();

        // Alice and Bob exchange public keys
        PublicKey alicePublicKey = aliceKeyPair.getPublic();
        PublicKey bobPublicKey = bobKeyPair.getPublic();

        // Alice and Bob compute the shared secret
        SecretKey aliceSharedSecret = computeSharedSecret(aliceKeyPair.getPrivate(), bobPublicKey.getEncoded());
        SecretKey bobSharedSecret = computeSharedSecret(bobKeyPair.getPrivate(), alicePublicKey.getEncoded());

        // The shared secret can now be used as the HMAC key
        byte[] message = "Hello, Bob!".getBytes();
        byte[] aliceHMAC = computeHMAC(aliceSharedSecret, message);
        byte[] bobHMAC = computeHMAC(bobSharedSecret, message);

        // Verify the HMACs
        boolean aliceVerified = verifyHMAC(aliceSharedSecret, message, aliceHMAC);
        boolean bobVerified = verifyHMAC(bobSharedSecret, message, bobHMAC);

        System.out.println("Alice's HMAC Verified: " + aliceVerified);
        System.out.println("Bob's HMAC Verified: " + bobVerified);
    }

    private static KeyPair generateKeyPair() throws Exception 
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(2048); // You can choose an appropriate key size
        return keyPairGenerator.generateKeyPair();
    }

    private static SecretKey computeSharedSecret(PrivateKey privateKey, byte[] publicKeyBytes) throws Exception 
    {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);

        return new SecretKeySpec(keyAgreement.generateSecret(), "AES");
    }

    private static byte[] computeHMAC(SecretKey key, byte[] message) throws Exception 
    {
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(key);
        return hmac.doFinal(message);
    }

    private static boolean verifyHMAC(SecretKey key, byte[] message, byte[] receivedHMAC) throws Exception 
    {
        byte[] computedHMAC = computeHMAC(key, message);
        return MessageDigest.isEqual(computedHMAC, receivedHMAC);
    }
}

