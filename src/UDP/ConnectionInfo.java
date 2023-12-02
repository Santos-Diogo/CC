package UDP;

/**
 * Class with paramenters relevant to store in the context of a given UDP connection
 */
public class ConnectionInfo 
{
    private int availableToTransfer;

    
    public ConnectionInfo (int availableToTransfer)
    {
        this.availableToTransfer= availableToTransfer;
    }
    
    public int getAvailableToTransfer() 
    {
        return availableToTransfer;
    }

    public void setAvailableToTransfer(int availableToTransfer) 
    {
        this.availableToTransfer = availableToTransfer;
    }
    
}
