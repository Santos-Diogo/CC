package FileInfo;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class NodeInfo {
    private InetAddress nodeIP;      
    private List<String> arquivos; // Lista de arquivos que este nó possui
    private List<String> blocosDisponiveis; // Lista de blocos disponíveis para armazenar mais arquivos

    public NodeInfo(InetAddress nodeIP) {
        this.nodeIP = nodeIP;
        this.arquivos = new ArrayList<>();
        this.blocosDisponiveis = new ArrayList<>();
    }

    public void adicionarArquivo(String nomeArquivo) { // Adicionar ficheiros ao Node
        if (!arquivos.contains(nomeArquivo)) {
            arquivos.add(nomeArquivo);
        }
    }

    public void removerArquivo(String nomeArquivo) {
        arquivos.remove(nomeArquivo);
    }

    public List<String> getArquivos() {
        return arquivos;
    }

    public boolean possuiArquivo(String nomeArquivo) { // Verifica se algum node tem o ficheiro que queremos
        for (String arquivo : arquivos) {
            if (arquivo.equals(nomeArquivo)) {
                return true;
            }
        }
        return false;
    }

    public List<String> consultarBlocosDisponiveis() {
        return new ArrayList<>(blocosDisponiveis);
    }

    public void atualizarBlocos(List<String> novosBlocos) {
        blocosDisponiveis.addAll(novosBlocos);
    }

    public List<NodeInfo> consultarFSTracker(String consulta) throws InterruptedException {
        List<NodeInfo> resposta = null;
        try {
            InetAddress fsTrackerAddress = InetAddress.getByName("endereco_do_tracker");
            int fsTrackerPort = 12345; // Porta do tracker

            Socket socket = new Socket(fsTrackerAddress, fsTrackerPort); // Criação de um socket para se
                                                                         // conectar ao FSTracker (Não sei
                                                                         // se a porta e endereço é isso)

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // pacote de consulta
                                                                                       // e envie para o
                                                                                       // FSTracker
            out.writeObject(consulta);

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); // resposta do FSTracker
            resposta = (List<NodeInfo>) in.readObject();

            // Fecha a conexão
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resposta;
    }
}