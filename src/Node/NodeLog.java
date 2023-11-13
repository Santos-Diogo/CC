package Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodeLog implements Serializable
{
    public enum InstructionType
    {
        ADD_F,
        RM_F
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
    private NodeInfo init_state;
    private List<Instruction> instructions;

    /**
     * Create the Log
     * @param state node state
     */
    public NodeLog (NodeInfo state)
    {
        this.init_state= state;
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
        this.init_state= state;
        this.instructions= new ArrayList<Instruction>(this.n_instructions);
    }

    /**
     * @return calculated state from initial state and following instructions
     */
    public NodeInfo get_state ()
    {
        //@TODO
        return this.init_state;
    }
}
