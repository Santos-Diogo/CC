package ver2.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import ver2.SharedState.SharedState;
import ver2.ThreadTools.ThreadControl;
import ver2.Track_Protocol.Track_Packet;

/**
 * Class responsible for handling communication for each independent node on the
 * server side.
 */
public class ServerCom implements Runnable {
    private static ThreadControl tc;
    private static Socket socket;
    private static ObjectInputStream input;
    private static SharedState ss;

    private void handle(Track_Packet packet) {
        switch (packet.getType()) {
            case REG:
                System.out.println("REG message");
                break;
            case ACK:
                System.out.println("ACK message");
                break;
            case AVF:
                System.out.println("AVF message");
                break;
            case GET:
                System.out.println("GET message");
                break;
            default:

        }

    }

    public ServerCom(Socket socket, ThreadControl tc, SharedState ss) throws IOException {
        this.socket = socket;
        this.tc = tc;
        this.ss = ss;
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
        Track_Packet packet;

        while (tc.get_running()== true) {
            try {
                System.out.println("WREPETUUUUU");
                packet = (Track_Packet) input.readObject();
                handle(packet);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("ServerCom Thread encountered a problem while reading from client");
                e.printStackTrace();
            }
        }
    }
}
