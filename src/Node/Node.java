package Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import Track_Protocol.TrackPacket;
import Track_Protocol.TrackPacket.TypeMsg;
import Payload.TrackPacketPayload.*;

/***
 * Main Node thread
 */
public class Node {

    private static ObjectOutputStream trackerOutput;
    private static ObjectInputStream trackerInput;
    private static InetAddress adress;
    private static Scanner scanner = new Scanner(System.in);

    private static void handle_avf() {
        try {
            // Write Request
            trackerOutput.writeObject(new TrackPacket(adress, TypeMsg.AVF_REQ, null));
            trackerOutput.flush();

            // Get response
            TrackPacket packet = (TrackPacket) trackerInput.readObject();

            // Write File Names
            AvfRepPacket payload = (AvfRepPacket) packet.getPayload();
            List<String> files = payload.get_files();
            for (String s : files) {
                System.out.println(s);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void handle_command(String command) {
        switch (command) {
            case "avf":
                handle_avf();
                break;
            default:
                break;
        }
    }

    private static String command_request() {
        System.out.println("Type your desired command:\navf - available files\nquit- exit the network\n");
        return scanner.nextLine();
    }

    public static void main(String[] args) throws InterruptedException {
        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try {
            // Define this machine IP adress
            adress = Inet4Address.getLocalHost();
            // Connects to server
            Socket socket = new Socket(serverAddress, serverPort);
            trackerOutput = new ObjectOutputStream(socket.getOutputStream());
	    trackerInput = new ObjectInputStream(socket.getInputStream());
            // Reg Message
            TypeMsg type = TypeMsg.REG;
            NodeInfo ndinfo = new NodeInfo(args[2]);
            System.out.println(ndinfo.get_file_blocks().toString());
            TrackPacket protocol = new TrackPacket(adress, type, new RegPacket(ndinfo.get_file_blocks()));
            trackerOutput.writeObject(protocol);
            trackerOutput.flush();

            String command;
            while (!(command = command_request()).equals("quit")) {
                handle_command(command);
            }

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
