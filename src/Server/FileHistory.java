package Server;

import java.util.HashMap;
import java.util.Map;

public class FileHistory {
    private Map<String, Long> files_size;
    private Map<String, Long> files_nBlocks;

    public FileHistory ()
    {
        this.files_size = new HashMap<>();
        this.files_nBlocks = new HashMap<>();
    }

    public void add_fileHistory (String file, Long filesize)
    {
        if(!files_size.containsKey(file)) //Used to make each Map entry final
        {
            files_size.put(file, filesize);
            // If the fileSize % blockSize is not 0 we must add another block otherwise we lose information about the file.
            files_nBlocks.put(file, (filesize % Shared.Defines.blockSize == 0 ) ? filesize/ Shared.Defines.blockSize : (filesize/ Shared.Defines.blockSize) + 1);
        }
    }

    /* 
    public void add_fileHistory (Map<String, Long> files_withSize)
    {
        files_withSize.entrySet().stream().map(entry -> this.files_size.put(entry.getKey(), entry.getValue()));
    }
    */
    public boolean contains_file (String file)
    {
        return files_size.containsKey(file);
    }

    /**
     * Unsafe method. Should only be used after confirming the file exists with 'contains_file'
     * @param file
     * @return
     */
    public Long get_fileNBlocks (String file)
    {
        return files_nBlocks.get(file);
    }
}
