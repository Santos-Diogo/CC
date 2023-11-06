package ver1.FSProtocol;
import ver1.FSNode.FSNode;
import java.net.*;
import java.io.*;


public class FSTrackerProtocol implements Serializable 
{
    private String nomeDoArquivo; // Pacotes REG
    private FSNode fsNode;        // Pacotes REG e ACK
    private String fileId;        // Pacotes ACK

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

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId(){
        return fileId;
    }



    public static FSTrackerProtocol createRegProtocol (InetAddress srcIP, FSNode fsNode, String nomeDoArquivo){
        byte[] regPayload = new byte[4];
        FSTrackerProtocol regPacket = new FSTrackerProtocol(regPayload,TypeMsg.REG, srcIP);
        regPacket.setNomeDoArquivo(nomeDoArquivo);
        regPacket.setFSNode(fsNode);
        return regPacket;
    }

    public static FSTrackerProtocol createAckPacket (InetAddress srcIP, FSNode fsNode, String fileId){
        byte[] ackPayload = new byte[4];
        FSTrackerProtocol ackPacket = new FSTrackerProtocol(ackPayload, TypeMsg.ACK, srcIP);
        ackPacket.setFSNode(fsNode);
        ackPacket.setFileId(fileId);
        return ackPacket;
    }
    
    public static FSTrackerProtocol createAvfPacket (InetAddress srcIP, FSNode fsNode){
        byte[] avfPayload = new byte[4];
        FSTrackerProtocol avfPacket = new FSTrackerProtocol(avfPayload,TypeMsg.AVF,srcIP);
        avfPacket.setFSNode(fsNode);
        return avfPacket;
    }

    public static FSTrackerProtocol createGetPacket (InetAddress srcIP, String fileId){
        byte[] getPayload = new byte[4]; //Pacotes GET
        FSTrackerProtocol getPacket = new FSTrackerProtocol(getPayload, TypeMsg.GET, srcIP);
        getPacket.setFileId(fileId);
        return getPacket;
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
