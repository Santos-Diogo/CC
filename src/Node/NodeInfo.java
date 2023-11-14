package Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class NodeInfo implements Serializable
{
    private List<String> files;
    private Map<String, Integer> files_size;
    private Map<String, List<Integer>> files_blocks;

    /**
     * @param dir Directory from wich we read the files
     * @apiNote If the file as all the blocks leave the List in files_blocks as 0(null) =)
     */
    public NodeInfo (String dir)
    {
        //handle meta-data file
        try (FileInputStream metadata = new FileInputStream(dir)) 
        {
            int n_bytes;
            while ((n_bytes = metadata.read()) != -1)
            {
                // Process the bytes as needed
                
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace(); // Handle potential IOException
        }
    }

    /**
     * Used in Serialization
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void writeObject (ObjectOutputStream out) throws IOException
    {
        //Write Files List
        out.writeInt(this.files.size());
        for (String s: this.files)
            out.writeUTF(s);
        
        //Write Maps
        out.writeObject(this.files_size);
        out.writeObject(this.files_blocks);
    }

    /**
     * Used in Serialization
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        //Read Files List
        int size_l= in.readInt();
        List<String> files= new ArrayList<>();
        for (int i= 0; i< size_l; i++)
            files.add((String) in.readObject());
        
        //Read Maps
        Map<String, Integer> files_size= (Map<String,Integer>) in.readObject();
        Map<String, List<Integer>> files_blocks= (Map<String,List<Integer>>) in.readObject();

        this.files= files;
        this.files_size= files_size;
        this.files_blocks= files_blocks;
    }

    /**
     * Add a file to the Structure
     * 
     * @param name  name of the file
     * @param size  size of the file in blocks
     * @param blocks    blocks owned. (null for all blocks)
     */
    public void add_file (String name, int size, List<Integer> blocks)
    {
        files.add (name);
        files_size.put (name, size);
        files_blocks.put (name, blocks);
    }

    /**
     * Remove a file with a given name from the node info
     * 
     * @param name name of the file
     */
    public void rm_file (String name)
    {
        files.removeIf(file -> file.equals(name));
        files_size.remove(name);
        files_blocks.remove(name);
    }

    /**
     * @return returns the names of the files stored
     */
    public List<String> get_files ()
    {
        return files;
    }

    /**
     * @param file name of the file
     * @return returns the number of blocks of a given file
     */
    public int get_file_size (String file)
    {
        return files_size.get(file);
    }

    /**
     * @param file name of the file
     * @return returns the blocks the node has of a given file
     */
    public List<Integer> get_file_blocks (String file)
    {
        return files_blocks.get(file);
    }
}