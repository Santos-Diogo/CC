package Blocker;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileBlockInfo
{
    private Map<String, BlockInfo> fileBlockInfo;


    /**
     * @param dir Directory from wich we read the files
     * @apiNote If the file has all the blocks leave the List in files_blocks as
     *          0(null) =)
     */
    public FileBlockInfo (String dir)
    {
        this.fileBlockInfo= new HashMap<>();

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

                    if (!fileBlockInfo.containsKey(fileName))
                        fileBlockInfo.put(fileName, new BlockInfo(null, new ArrayList<>()));
                    fileBlockInfo.get(fileName).add_block(Integer.parseInt(matcher.group(2)));
                }
                else 
                {
                    Long fileSize = Files.size(filePath);
                    fileBlockInfo.put(fileName, new BlockInfo(fileSize, null));
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
        fileBlockInfo.put(name, new BlockInfo(size, blocks));
    }

    /**
     * Remove a file with a given name from the node info
     * 
     * @param name name of the file
     */
    public void rm_file(String name) 
    {
        fileBlockInfo.remove(name);
    }

    /**
     * @return returns the names of the files stored
     */
    public List<String> get_files() 
    {
        return fileBlockInfo.keySet().stream().toList();
    }

    public BlockInfo get_blockInfo (String file)
    {
        return fileBlockInfo.get(file);
    }

    public Map<String, BlockInfo> get_fileBlockInfo ()
    {
        return fileBlockInfo;
    }
}
