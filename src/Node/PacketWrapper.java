package Node;

import java.net.DatagramPacket;
import java.net.InetAddress;

import Shared.CRC;
import UDP.TransferProtocol.TransferPacket;

public class PacketWrapper 
{
    static public DatagramPacket datagramMaker (InetAddress target,
                                                int port,
                                                byte[] encryptedPayload,
                                                boolean fromClient,
                                                TransferPacket.TypeMsg typeMsg) throws Exception
    {
        //Make final message with encrypted payloada
        TransferPacket transferPacket= new TransferPacket(TransferPacket.TypeMsg.GET, true, encryptedPayload);

        //Add sumcheck to serialized packet
        byte[] sumCheckedPacket= CRC.couple(transferPacket.serialize());
        return new DatagramPacket(sumCheckedPacket, sumCheckedPacket.length, target, port);
    }
}
