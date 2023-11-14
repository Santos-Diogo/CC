package Server;

import Server.ServerInfo;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class ServerLog implements Serializable
{
    /**
     * Types of Instructions
     */
    public enum InstructionType
    {
        ADD_F,  //ADD FILE
        ADD_B   //ADD BLOCK
    }

    /**
     * Notion of an Instruction from the Log perspective
     */
    public class Instruction
    {
        InstructionType type;
        byte[] args;

        public Instruction (InstructionType type, byte[] args)
        {
            this.type= type;
            this.args= args;
        }
    }
    
    public class State_Exception extends Exception
    {
        public State_Exception()
        {
            super("Instructions past Limit"); 
        }
    }

    private final int n_instructions= 25;
    private ServerInfo state;
    private List<Instruction> instructions;

    /**
     * Create the Log
     * @param state node state
     */
    public ServerLog (ServerInfo state)
    {
        this.state= state;
        this.instructions= new ArrayList<>(this.n_instructions);
    }

    /**
     * Used in Serialization
     * @param out
     * @throws IOException
     */
    private void writeObject (ObjectOutputStream out) throws IOException
    {
        //Write the state
        out.writeObject(this.state);

        //Write the Instruction List
        out.writeInt(this.instructions.size());
        for (Instruction i: this.instructions)
        {
            out.writeObject(i.type);
            out.writeInt(i.args.length);
            out.write(i.args);
        }
    }

    /**
     * Used in Serialization
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        //Read the Server state
        this.state= (ServerInfo) in.readObject();

        //Build the Instruction List
        this.instructions= new ArrayList<>();
        int N= in.readInt();
        Instruction instruction= new Instruction(null, null);

        for (int i= 0; i< N; i++)
        {
            instruction.type= (InstructionType) in.readObject();

            //Read the size of and the array of bytes
            int size_a= in.readInt();
            instruction.args= in.readNBytes(size_a);

            //Add instruction to the List
            this.instructions.add(new Instruction(instruction.type, instruction.args));
        }
    }

    /**
     * Add an instruction to the log. throws exception if the number n of instructions in the list has been exceeded
     * @param i instruction to add to the log
     * @throws State_Exception
     */
    public void add_instruction (Instruction i) throws State_Exception
    {
        if (this.instructions.size()== this.n_instructions)
        {
            throw new State_Exception();
        }
        
        this.instructions.add(i); 
    }

    /**
     * Update the state stored in the log
     * @param state state
     */
    public void update_state (ServerInfo state)
    {
        this.state= state;
        this.instructions= new ArrayList<>(this.n_instructions); 
    }

    /**
     * Applies an instruction to the state
     * @param n
     * @param instruction
     * @throws IOException
     */
    public void apply_instruction (ServerInfo n, Instruction instruction) throws IOException
    {
        DataInputStream s= new DataInputStream(new ByteArrayInputStream(instruction.args));

        switch (instruction.type)
        {
            case ADD_F:
                String name_f= s.readUTF();
                InetAddress= s.readObject();
                n.add_file(name_f, null, null);
                break;
        }
    }

    /**
     * This method also updates the state
     * @return calculated state from initial state and following instructions
     */
    public ServerInfo get_state ()
    {
        
    }
}
