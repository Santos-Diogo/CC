package Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.HashMap;

public class NodeInfo
{
    private List<String> files;
    private Map<String, Long> files_size;
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

        //This part is very early production, it will NOT work
        //Something similar to this will be done/this snipet will be used
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dir)))
        {
            for (Path filePath : directoryStream)
            {
                String fileName = filePath.getFileName().toString();
                Long fileSize = Files.size(filePath);
                files.add(fileName);
                files_size.put(fileName, fileSize);
                int blocks = (int) ((fileSize % 1024 == 0) ? fileSize / 1024 : (fileSize / 1024) + 1);
                files_blocks.put(fileName, IntStream.rangeClosed(0, blocks).boxed().collect(Collectors.toList()));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add a file to the Structure
     * 
     * @param name  name of the file
     * @param size  size of the file in blocks
     * @param blocks    blocks owned. (null for all blocks)
     */
    public void add_file (String name, long size, List<Integer> blocks)
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
     * @return returns the file size
     */
    public long get_file_size (String file)
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