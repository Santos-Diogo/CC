package Node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Blocker.FileBlockInfo;

public class ConcurrentUDPServer {
    
    private static final int THREAD_POOL_SIZE = 10;
    private FileBlockInfo fbi;

    public ConcurrentUDPServer (FileBlockInfo fbi)
    {
        this.fbi = fbi;
    }

    public void startServer () {
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
        String receivedMessage = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received message from " + packet.getAddress() + ": " + receivedMessage);

        // Process the received packet (replace this with your logic)

        // Send a response back to the client if needed
        // sendResponse(packet.getAddress(), packet.getPort(), "Response message");
    }

    // Method to send a UDP response
    private static void sendResponse(/*InetAddress address, int port, String message*/) {
        // Implement the logic to send a response back to the client
    }
}
