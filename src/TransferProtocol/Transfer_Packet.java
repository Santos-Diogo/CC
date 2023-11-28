package TransferProtocol;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Set;

public class Transfer_Packet implements Serializable {
    public enum TypeMsg {           // TypeMsg mas da transfer e não do tracker
        GETF,                       // Get file
        FT,                         // File Transfer
        ACK,
        ERR                         // Error 
    }


    private InetAddress src_ip;
    private TypeMsg type;
    private byte[] payload;
    private String nome_ficheiro;
    private Set<Integer> blocos_inseridos;      //Assim só tenho blocos únicos (sem repetições)

    public Transfer_Packet (InetAddress src_ip, TypeMsg type, byte[] payload, String nome_ficheiro, Set<Integer> blocos_inseridos){
        this.src_ip = src_ip;
        this.type = type;
        this.payload = payload;
        this.nome_ficheiro = nome_ficheiro;
        this.blocos_inseridos = blocos_inseridos;       
    }

    public Transfer_Packet(InetAddress src_ip,TypeMsg type){
        this.src_ip = src_ip;
        this.type = type;
    }

    public InetAddress getSrc_ip(){
        return src_ip;
    }

    public TypeMsg getType(){
        return type;
    }

    public byte[] getPayload(){
        return payload;
    }

    public String getName(){
        return nome_ficheiro;
    }

    public Set<Integer> getBlocos(){
        return blocos_inseridos;
    }
}


