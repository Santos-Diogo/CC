package Node;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

import Block.BlockInfo;

public class NodeInfo 
{
    private List<String> files;
    private Map<String, Long> files_size;
    private Map<String, List<Integer>> files_blocks;

    private Map<String, BlockInfo> filesBlockInfo;

    /**
     * @param dir Directory from wich we read the files
     * @apiNote If the file has all the blocks leave the List in files_blocks as
     *          0(null) =)
     */
    public NodeInfo(String dir) 
    {
        this.files = new ArrayList<>();
        this.files_size = new HashMap<>();
        this.files_blocks = new HashMap<>();

        this.filesBlockInfo= new HashMap<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dir))) 
        {
            for (Path filePath : directoryStream) 
            {
                String fileName = filePath.getFileName().toString();
                Pattern pattern = Pattern.compile("^(.+)\\.fsblk\\.(\\d+)$");
                Matcher matcher = pattern.matcher(fileName);

                if (matcher.matches())
                {
                    fileName = matcher.group(1);
                    
                    if (!files.contains(fileName))
                        files.add(fileName);

                    if (!filesBlockInfo.containsKey(fileName))
                        filesBlockInfo.put(fileName, new BlockInfo(null, new ArrayList<>()));
                    filesBlockInfo.get(fileName).add_block(Integer.parseInt(matcher.group(2)));
                }
                else 
                {
                    Long fileSize = Files.size(filePath);
                    files.add(fileName);
                    files_size.put(fileName, fileSize);
                    files_blocks.put(fileName, null);
                }
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
     * @param name   name of the file
     * @param size   size of the file in blocks
     * @param blocks blocks owned. (null for all blocks)
     */
    public void add_file(String name, long size, List<Integer> blocks) 
    {
        files.add(name);
        files_size.put(name, size);
        files_blocks.put(name, blocks);
    }

    /**
     * Remove a file with a given name from the node info
     * 
     * @param name name of the file
     */
    public void rm_file(String name) 
    {
        files.removeIf(file -> file.equals(name));
        files_size.remove(name);
        files_blocks.remove(name);
    }

    /**
     * @return returns the names of the files stored
     */
    public List<String> get_files() 
    {
        return files;
    }

    /**
     * @param file name of the file
     * @return returns the file size
     */
    public long get_file_size(String file) 
    {
        return files_size.get(file);
    }

    /**
     * @param file name of the file
     * @return returns the blocks the node has of a given file
     */
    public List<Integer> get_blocks_by_file(String file) 
    {
        return files_blocks.get(file);
    }

    public Map<String, List<Integer>> get_file_blocks() 
    {
        return files_blocks;
    }
}