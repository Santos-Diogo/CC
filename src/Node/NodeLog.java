package Node;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used for keeping an image of the server state
 */
public class NodeLog implements Serializable
{
    /**
     * Types of Instructions
     */
    public enum InstructionType
    {
        ADD_F,  //ADD FILE
        RM_F    //REMOVE FILE
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
    private NodeInfo state;
    private List<Instruction> instructions;

    /**
     * Create the Log
     * @param state node state
     */
    public NodeLog (NodeInfo state)
    {
        this.state= state;
        this.instructions= new ArrayList<Instruction>(n_instructions);
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
        out.writeInt(instructions.size());
        for (Instruction i: instructions)
        {
            //Write Type
            out.writeObject(i.type);
            //Write the size of and the array of bytes
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
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        //Read the state
        this.state= (NodeInfo) in.readObject();

        //Read the Instruction List
        this.instructions= new ArrayList<Instruction>(n_instructions);

        //Read size and build Instruction List
        int N= in.readInt();
        Instruction instruction= new Instruction(null, null);

        for (int i= 0; i< N; i++)
        {
            //Read Type
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
    public void update_state (NodeInfo state)
    {
        this.state= state;
        this.instructions= new ArrayList<Instruction>(this.n_instructions);
    }

    public void apply_instruction (NodeInfo n, Instruction instruction) throws IOException
    {
        DataInputStream s= new DataInputStream(new ByteArrayInputStream(instruction.args));

        switch (instruction.type)
        {
            case ADD_F:
                String name_f= s.readUTF();
                int size_b= s.readInt();
                int size_l= s.readInt();
                List<Integer> blocks= new ArrayList<Integer>(size_l);

                //Compose the List
                for (int i= 0; i< size_l; i++)
                {
                    blocks.add(s.readInt());
                }
                
                //Add the file
                n.add_file (name_f, size_b, blocks);
                break;

            case RM_F:
                name_f= s.readUTF();

                n.rm_file (name_f);
                break;
        }
    }

    /**
     * This method also updates the state
     * @return calculated state from initial state and following instructions
     */
    public NodeInfo get_state ()
    {
        for (Instruction i : this.instructions)
        {
            try
            {
                apply_instruction (this.state, i);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        this.instructions= new ArrayList<Instruction>(this.n_instructions);
        return this.state;
    }
}
