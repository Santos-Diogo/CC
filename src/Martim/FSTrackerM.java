package Martim;
import java.util.*;
import Martim.*;

public class FSTrackerM {
    private List<FSNodeM> fsNodes; // Lista de todos os FSNodes registados
    private Map<String, List<FSNodeM>> arquivoParaFSNodes;


    public FSTrackerM(){
        fsNodes = new ArrayList<>();
        arquivoParaFSNodes = new HashMap<>();
    }

    public void adicionarFSNode(FSNodeM fsNode){            //Adicionar Node ao Tracker
        if(!fsNodes.contains(fsNode)){
            fsNodes.add(fsNode);
        }
    }

    public void removerFSNode(FSNodeM fsNode){
        fsNodes.remove(fsNode);
    }

    public List<FSNodeM> consultarNodesComArquivo(String nomeArquivo) {         // Retorna uma lista de FSNodes que possuem o arquivo especificado
        List<FSNodeM> nodesComArquivo = new ArrayList<>();
        for(FSNodeM Node : fsNodes){
            if (Node.possuiArquivo(nomeArquivo)){
                nodesComArquivo.add(Node);
            }
        }
        return nodesComArquivo;  
    }

    public void atualizarInformacoes(){
        arquivoParaFSNodes.clear();
        for(FSNodeM fsNode : fsNodes){
            for(String arquivo : fsNode.getArquivos()){
                arquivoParaFSNodes.computeIfAbsent(arquivo, k -> new ArrayList<>()).add(fsNode);
            }
        }
    }

    public Map<String, List<FSNodeM>> getArquivoParaFSNode(){                       // Diz-nos que ficheiros estão em cada FSNode
        return arquivoParaFSNodes;
    }
}








