package Test;

import Shared.CRC;
import java.util.Arrays;

public class test 
{
    static boolean testCRC ()
    {
        String string= "teste teste teste";
        byte[] serialized= string.getBytes();

        byte[] checked= CRC.couple(serialized);

        byte[] recovered= CRC.decouple(checked);

        return Arrays.equals(serialized, recovered);
    }

    public static void main (String[] args)
    {
        boolean tests= testCRC();
        System.out.println("Tests results:"+ tests);
    }    
}
