package Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

public class NodeLog implements Serializable
{
    public enum InstructionType
    {
        ADD_F,  //ADD FILE
        RM_F    //REMOVE FILE
    }

    public class Instruction
    {
        InstructionType type;
        byte[] args;
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
     * Add an instruction to the log
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

    public void apply_instruction (NodeInfo n, Instruction i)
    {
        switch (i.type)
        {
            case ADD_F:
            //conseguir apanhar os argumentos a partir de um byte[]
            //n.add_file ();
            break;
            case RM_F:
            //n.rm_file ();
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
            apply_instruction (this.state, i);
        }
        this.instructions= new ArrayList<Instruction>(this.n_instructions);
        return this.state;
    }
}
