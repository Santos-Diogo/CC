package ver2.Node;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

import ver2.Track_Protocol.Track_Packet;
import ver2.Track_Protocol.Track_Packet.TypeMsg;

/***
 * Main Node thread
 */
public class Node {

    private static ObjectOutputStream 
    
    public static void main(String[] args) throws InterruptedException {
        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try {
            // Connects to server
            Socket socket = new Socket(serverAddress, serverPort);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            // temp
            byte[] payload = new byte[4];

            TypeMsg type = TypeMsg.REG;
            Track_Packet protocol = new Track_Packet(Inet4Address.getLocalHost(), type, payload);
            System.out.println(Inet4Address.getLocalHost().toString());
            // Serialize and send the protocol object
            outputStream.writeObject(protocol);
            /*
                * Thread.sleep(5000);
                * outputStream.writeObject(protocol);
                */

            // Close the socket when done
            socket.close();
        } catch (UnknownHostException e) {
            System.out.println(serverAddress + " Is not a valid adress");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tem de ter um Node-Handler para receber os pedidos dos outros nodes e dedicar
        // a cada um uma instancia de Node_Communication
    }
}
