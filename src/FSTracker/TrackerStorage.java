package FSTracker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import FSNode.FSNode;
import FSProtocol.FSTrackerProtocol;

public class TrackerStorage {
    private final Map<String,List<FSNode>> fileToNodesMap = new HashMap<>();
    private final Map <FSNode,List<String>> nodeToFilesMap = new HashMap<>();
    private static PacketManager pm;
    private String nomeDoArquivo;
    private FSNode fsNode;


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


    public static void main(String[] args){
        TrackerStorage trackerStorage = new TrackerStorage();
        FSTrackerProtocol receivedPacket = pm.get_packet();
        FSTrackerProtocol.TypeMsg packetType = receivedPacket.getTypeMsg();
        if (packetType == FSTrackerProtocol.TypeMsg.REG) {
            String nomeArquivo = receivedPacket.getNomeArquivo();
            FSNode fsNode = receivedPacket.getFSNode();
            trackerStorage.adicionarArquivoAoFSNode(nomeArquivo,fsNode);                      // adicionarArquivoAoFSNode ainda tem de ser implementado e serve para atualizar as estruturas de dados!
        }
    }


}
