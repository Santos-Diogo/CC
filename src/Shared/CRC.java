package Shared;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * Class that implements methods for easy CRC32 message coupling and decoupling/checking
 */
public class CRC 
{
    /**
     * @param message Message and CRC32 calc at the head
     * @return return null for failure and content if CRC32 matches up
     */
    static public byte[] decouple(byte[] message) {
        // Check the message's length
        if (message.length < 8)
            return null;

        // Get stored CRC32 result
        ByteBuffer byteBuffer = ByteBuffer.wrap(message);
        long stored = byteBuffer.getLong();

        // Ready up result
        int length = message.length - 8;
        byte[] result = new byte[length];
        
        // get the message
        byteBuffer.get(result);

        // Check CRC32
        CRC32 crc32 = new CRC32();
        crc32.update(result);
        long computed = crc32.getValue();

        return (stored == computed) ? result : null;
    }

    /**
     * @param message Input message
     * @return returns message coupled with CRC32 integrity check @ head
     */
    static public byte[] couple (byte[] message)
    {
        CRC32 crc32 = new CRC32();
        crc32.update(message);

        ByteBuffer byteBuffer = ByteBuffer.allocate(message.length + 8);
        byteBuffer.putLong(crc32.getValue());
        byteBuffer.put(message);

        return byteBuffer.array();
    }
}

