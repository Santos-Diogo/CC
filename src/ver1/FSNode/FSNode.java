package ver1.FSNode;
import java.io.*;
import java.net.*;

import ver1.FSProtocol.FSTrackerProtocol;
import ver1.FSProtocol.FSTrackerProtocol.TypeMsg;;

public class FSNode {

    public static void main(String[] args) {
        String serverAddress = args[0]; // Change to the server's IP address or hostname
        int serverPort = Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            //temp
            byte [] payload= new byte[4];

            TypeMsg type= TypeMsg.REG;
            FSTrackerProtocol protocol = new FSTrackerProtocol(payload, type, Inet4Address.getLocalHost());

            // Serialize and send the protocol object
            outputStream.writeObject(protocol);

            // Close the socket when done
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
