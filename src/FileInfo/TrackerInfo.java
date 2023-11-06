package FileInfo;

import java.util.*;

public class TrackerInfo {
    private List<NodeInfo> fsNodes; // Lista de todos os FSNodes registados
    private Map<String, List<NodeInfo>> arquivoParaFSNodes;

    public TrackerInfo() {
        fsNodes = new ArrayList<>();
        arquivoParaFSNodes = new HashMap<>();
    }

    public void adicionarFSNode(NodeInfo fsNode) { // Adicionar Node ao Tracker
        if (!fsNodes.contains(fsNode)) {
            fsNodes.add(fsNode);
        }
    }

    public void removerFSNode(NodeInfo fsNode) {
        fsNodes.remove(fsNode);
    }

    public List<NodeInfo> consultarNodesComArquivo(String nomeArquivo) { // Retorna uma lista de FSNodes que possuem o
                                                                         // arquivo especificado
        List<NodeInfo> nodesComArquivo = new ArrayList<>();
        for (NodeInfo Node : fsNodes) {
            if (Node.possuiArquivo(nomeArquivo)) {
                nodesComArquivo.add(Node);
            }
        }
        return nodesComArquivo;
    }

    public void atualizarInformacoes() {
        arquivoParaFSNodes.clear();
        for (NodeInfo fsNode : fsNodes) {
            for (String arquivo : fsNode.getArquivos()) {
                arquivoParaFSNodes.computeIfAbsent(arquivo, k -> new ArrayList<>()).add(fsNode);
            }
        }
    }

    public Map<String, List<NodeInfo>> getArquivoParaFSNode() { // Diz-nos que ficheiros estão em cada FSNode
        return arquivoParaFSNodes;
    }
}