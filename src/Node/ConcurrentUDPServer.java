package Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Blocker.FileBlockInfo;
import ThreadTools.ThreadControl;
import TransferProtocol.GetFilesReq;
import TransferProtocol.TransferPacket;

public class ConcurrentUDPServer implements Runnable{
    
    private static final int THREAD_POOL_SIZE = 10;
    private ThreadControl tc;
    private FileBlockInfo fbi;

    public ConcurrentUDPServer (FileBlockInfo fbi, ThreadControl tc)
    {
        this.fbi = fbi;
        this.tc = tc;
    }

    public static void startServer () {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (DatagramSocket serverSocket = new DatagramSocket(Shared.Defines.transferPort)) {
            System.out.println("Concurrent UDP Server listening on port " + Shared.Defines.transferPort);

            while (true) {
                byte[] receiveData = new byte[2048];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                // Blocking call, waits for a UDP packet to arrive
                serverSocket.receive(receivePacket);

                // Submit the packet handling task to the thread pool
                executorService.submit(() -> handlePacket(receivePacket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private static void handlePacket(DatagramPacket packet) {
        try {
            TransferPacket receivedMessage = TransferPacket.deserialize(packet.getData());
            switch (receivedMessage.type) {
                case GETF_REQ:
                    GetFilesReq parsedPacket = (GetFilesReq) receivedMessage;
                    System.out.println("Received message from " + packet.getAddress() + ": " + parsedPacket.file + " " + parsedPacket.blocks.toString());
                    break;
            
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Process the received packet (replace this with your logic)

        // Send a response back to the client if needed
        // sendResponse(packet.getAddress(), packet.getPort(), "Response message");
    }

    // Method to send a UDP response
    private static void sendResponse(/*InetAddress address, int port, String message*/) {
        // Implement the logic to send a response back to the client
    }

    public void run ()
    {
        while(tc.get_running() == true)
            startServer();
    }
}
