import java.io.*;
import java.net.*;

public class FSNode {
    
    public static void main (String[] args) {
        String serverAddress = args[0]; // Change to the server's IP address or hostname
        int serverPort = Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            // Create an instance of FSTrackerProtocol with your data
            byte[] payload = "Registo".getBytes();
            short type = 1; // Replace with the actual type
            InetAddress srcIP = InetAddress.getLocalHost(); // Replace with the actual source IP
            FSTrackerProtocol protocol = new FSTrackerProtocol(payload, type, srcIP);

            // Serialize and send the protocol object
            outputStream.writeObject(protocol);

            // Close the socket when done
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
