package FSTracker_Store;
import java.util.HashMap;
import java.util.Map;

import FSNode.FSNode;
import FSProtocol.FSTrackerProtocol;

public class TrackerStorage {
    private Map<String,List<FSNode>> fileToNodesMap = new HashMap<>();
    private Map <FSNode,List<String>> nodeToFilesMap = new HashMap<>();

    public static void main(String[] args){
        FSTrackerProtocol receivedPacket = packetManager.get_packet();
        short packetType = receivedPacket.getType();
        if (packetType == Registo_Arquivo) {
            String nomeArquivo = receivedPacket.getNomeArquivo();              //getNomeArquivo ainda tem de ser implementada
            FSNode fsNode = receivedPacket.getFSNode();                       //getFSNode ainda tem de ser implementada    
            adicionarArquivoAoFSNode(nomeArquivo,FSNode);                      // adicionarArquivoAoFSNode ainda tem de ser implementado e serve para atualizar as estruturas de dados!
    }
}
