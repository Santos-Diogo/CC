package FileInfo;

import java.util.List;
import java.util.Map;

public class NodeInfo
{
    private List<String> files;
    private Map<String, Integer> files_size;
    private Map<String, List<Integer>> files_blocks;

    /**
     * @param dir Direcotry from wich we read the files
     * @apiNote If the file as all the blocks leave the List in files_blocks as 0(null) =)
     */
    public NodeInfo (String dir)
    {
        //handle meta-data file
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