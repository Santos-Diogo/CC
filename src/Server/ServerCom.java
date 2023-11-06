package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import SharedState.SharedState;
import ThreadTools.ThreadControl;
import Track_Protocol.Track_Packet;

/**
 * Class responsible for handling communication for each independent node on the
 * server side.
 */
public class ServerCom implements Runnable {
    private ThreadControl tc;
    private Socket socket;
    private ObjectInputStream input;
    private SharedState ss;

    private void handle(Track_Packet packet) {
        switch (packet.getType()) {
            case REG:
                System.out.println("REG message");
                break;
            case AVF_REQ:
                System.out.println("AVF request message");
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

        while (tc.get_running() == true) {
            try {
                packet = (Track_Packet) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                break;
            }
            handle(packet);
        }
    }
}
