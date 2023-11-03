package ver1.FSProtocol;
import java.net.*;
import java.io.*;

public class FSTrackerProtocol implements Serializable 
{
    private String nomeDoArquivo;
    private FSNode fsNode;

    public void setNomeDoArquivo(String nomeDoArquivo) {
        this.nomeDoArquivo = nomeDoArquivo;
    }

    public String getNomeArquivo(){
        return nomeDoArquivo;
    }

    public void setFSNode(FSNode fsNode) {
        this.fsNode = fsNode;
    }

    public FSNode getFSNode(){
        return fsNode;
    }

    public enum TypeMsg
    {
        REG,
        ACK,
        AVF,
        GET
    };

    //Packages are immutable
    private final InetAddress srcIP; // Endereço IP de origem
    private final TypeMsg type;
    private final byte[] payload;

    @Override
    public String toString ()
    {
        return "srcIP="+ srcIP+ " type="+ type; 
    }

    public FSTrackerProtocol(byte[] payload, TypeMsg type, InetAddress srcIP)
    {
        this.payload = payload;
        this.type = type;
        this.srcIP = srcIP;
    }

    public InetAddress getSrcIP()
    {
        return srcIP;
    }

    public TypeMsg getTypeMsg()
    {
        return type;
    }

    public byte[] getPayload()
    {
        return payload;
    }

}
