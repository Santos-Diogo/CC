package Node;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Class responsible for handling incoming connections from other nodes (transfers)
 */
public class NodeHost 
{
    ServerSocket hub;

    public NodeHost ()
    {
        try
        {
            this.hub= new ServerSocket(NodeDefines.interNodePort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
