package Node;

import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Track_Protocol.Track_Packet;
import Track_Protocol.Track_Packet.TypeMsg;

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
            trackerOutput.writeObject(new Track_Packet(adress, TypeMsg.AVF_REQ));
            Track_Packet files = (Track_Packet) trackerInput.readObject();
            String list_of_files = new String(files.getPayload(), "UTF-8");
            System.out.println("Available files to download:\n" + list_of_files);
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

            TypeMsg type = TypeMsg.REG;
            Track_Packet protocol = new Track_Packet(adress, type);
            // Serialize and send the protocol object
            trackerOutput.writeObject(protocol);

            String command;
            while (!(command = command_request()).equals("quit")) {
                handle_command(command);
            }
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
