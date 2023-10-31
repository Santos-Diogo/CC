package Server;

public class Server 
{
    public void main (String[] args)
    {
        //Recebe as comunicações de cada node ou delega alguma thread para o fazer

        //Cada Node que estabelece ligação instancia uma thread NodeHandler que se encarrega da comunicação com esse Node

        //As threads partilham o estado do Servidor (ficheiros disponiveis, blocos em cada node, etc)
    }
}
