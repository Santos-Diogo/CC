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
import java.io.Serializable;


public class FileBlockInfo implements Serializable
{
    private Map<String, BlockInfo> fileBlockInfo;
    private Map<String, Long> files_filesize;


    /**
     * @param dir Directory from wich we read the files
     * @apiNote If the file has all the blocks leave the List in files_blocks as
     *          0(null) =)
     */
    public FileBlockInfo (String dir)
    {
        this.fileBlockInfo= new HashMap<>();
	this.files_filesize = new HashMap<>();
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
                        fileBlockInfo.put(fileName, new BlockInfo(new ArrayList<>()));
                    fileBlockInfo.get(fileName).add_block(Long.parseLong(matcher.group(2)));
                }
                else 
                {
                    files_filesize.put(fileName, Files.size(filePath));
                    fileBlockInfo.put(fileName, new BlockInfo(null));
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
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

    public Long get_filesize (String file)
    {
        return (files_filesize.containsKey(file)) ? files_filesize.get(file) : null;
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
