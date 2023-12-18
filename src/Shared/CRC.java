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
    static public byte[] decouple (byte[] message)
    {
        //Get stored CRC32 result
        ByteBuffer byteBuffer = ByteBuffer.wrap(message);
        long stored= byteBuffer.getLong();

        //Ready up result
        int length= message.length- 8;
        byte[] result= new byte[length];
        byteBuffer.get(result, 0, length);

        //Check CRC32
        CRC32 crc32= new CRC32();
        crc32.update(result);
        long computed= crc32.getValue();
        
        return (stored== computed) ? result : null;
    }

    /**
     * @param message Input message
     * @return returns message coupled with CRC32 integrity check @ head
     */
    static public byte[] couple (byte[] message)
    {
        //Calculate CRC32
        CRC32 crc32= new CRC32();
        crc32.update(message);
        long computed= crc32.getValue();

        //Write CRC32 @ head
        byte[] ret= new byte [message.length+ 8];
        ByteBuffer byteBuffer= ByteBuffer.wrap(ret);
        byteBuffer.putLong(computed);
        
        //Write the rest of the message
        System.arraycopy(message, 0, ret, 8, message.length);

        return ret;
    }
}

