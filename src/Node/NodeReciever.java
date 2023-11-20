package Node;

import java.io.InputStream;

public class NodeReciever implements Runnable
{
    private static InputStream in;      //Stream to read
    private static String file;         //File to store

    public NodeReciever (InputStream stream, String filename)
    {
        in= stream;
        file= filename;
    }

    public void run ()
    {
        try
        {
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
