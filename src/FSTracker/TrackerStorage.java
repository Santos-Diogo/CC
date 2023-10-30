package FSTracker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import FSNode.FSNode;
import FSProtocol.FSTrackerProtocol;

public class TrackerStorage {
    private Map<String,List<FSNode>> fileToNodesMap = new HashMap<>();
    private Map <FSNode,List<String>> nodeToFilesMap = new HashMap<>();
    private static PacketManager pm;
    private String nomeDoArquivo;

    public void adicionarArquivoAoFSNode (String nomeArquivo,FSNode fsNode){
        if(fileToNodesMap.containsKey(nomeArquivo)){
            List<FSNode> fsNodes = fileToNodesMap.get(nomeArquivo);
            fsNodes.add(fsNode);
        }
        else{
            List<FSNode> fsNodes = new ArrayList<>();
            fsNodes.add(fsNode);
            fileToNodesMap.put(nomeArquivo,fsNodes);
        }
    }

    public void setNomeDoArquivo(String nomeDoArquivo) {
        this.nomeDoArquivo = nomeDoArquivo;
    }

    public String getNomeArquivo(){
        return nomeDoArquivo
    }



    public static void main(String[] args){
        FSTrackerProtocol receivedPacket = pm.get_packet();
        FSTrackerProtocol.TypeMsg packetType = receivedPacket.getTypeMsg();
        if (packetType == FSTrackerProtocol.TypeMsg.REG) {
            String nomeArquivo = receivedPacket.getNomeArquivo();              //getNomeArquivo ainda tem de ser implementada
            FSNode fsNode = receivedPacket.getFSNode();                        //getFSNode ainda tem de ser implementada    
            adicionarArquivoAoFSNode(nomeArquivo,FSNode);                      // adicionarArquivoAoFSNode ainda tem de ser implementado e serve para atualizar as estruturas de dados!
        }
    }


}
