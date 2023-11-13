package Transfer_Protocol;
import java.net.InetAddress;

public class Transfer_Protocol{
    public enum TypeMsgT {
        GETF,                       // Get file
        FT,                         // File Transfer
        ACK,                       
        ERR                         // Error 
    }


    private InetAddress src_ip;
    private TypeMsgT typeT;
    private byte[] payload;
    private String nome_ficheiro;
    private List<Integer> blocos_inseridos;

    public Transfer_Packet (InetAddress src_ip, TypeMsgT typeT, byte[] payload, String nome_ficheiro, List<Integer> blocos_inseridos){
        this.src_ip = src_ip;
        this.TypeMsgT = typeT;
        this.payload = payload;
        this.nome_ficheiro = nome_ficheiro;
        this.blocos_inseridos = blocos_inseridos;       //Está mal!
    }


}


