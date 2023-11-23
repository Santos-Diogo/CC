package Server;

import java.util.*;

import Blocker.BlockInfo;
import Shared.NetId;

public class ServerInfo
{
    private class ServerBlockInfo extends BlockInfo
    {
        NetId netId;
        
        public ServerBlockInfo (BlockInfo bInfo, NetId netId)
        {
            super(bInfo);
            this.netId= netId;
        }
        
        public NetId get_netId ()
        {
            return this.netId;
        }
    }
    /**
     * Has to be concurrency ready
     */
    private class FileInfo
    {
        List<ServerBlockInfo> sbiList;
        Long fileSize;

        FileInfo ()
        {
            this.sbiList= new ArrayList<>();
            this.fileSize= null;
        }

        void set_fileSize (Long size)
        {
            if (size!= null)
                this.fileSize= size;
        }

        void add_list (ServerBlockInfo sbi)
        {
            this.sbiList.add(sbi);
        }

        /**
         * Removes a ServerBlockInfo with a given netId
         * @param id
         */
        /* void rm_list (NetId id)
        {
            this.sbiList.removeIf();
        } */

        Long get_fileSize()
        {
            return this.fileSize;
        }
    }
    
    Map<String, FileInfo> file_nodeData;

    public ServerInfo ()
    {
        this.file_nodeData= new HashMap<>();
    }

    public void add_file (String fileName, NetId netId, BlockInfo bInfo)
    {
        FileInfo f;
        //No file with that name exists
        if (!this.file_nodeData.containsKey(fileName))
        {
            this.file_nodeData.put(fileName, f= new FileInfo());
        }
        //Get the list to add that entry
        else
        {
            f= this.file_nodeData.get(fileName);
        }

        //Add info from node about file
        f.add_list(new ServerBlockInfo(bInfo, netId));
        //Set FileSize
        f.set_fileSize(bInfo.get_nBlocks());
    }

    public List<String> get_files ()
    {
        return this.file_nodeData.keySet().stream().toList();
    }

    public Long get_nBlocks (String file)
    {
        return this.file_nodeData.get(file).fileSize;
    }
    
    /**
     * Returns a map that maps nodes to the blocks they own of a given file
     * @param file
     * @return
     */
    public Map<NetId,List<Integer>> get_nodeInfoFile (String file)
    {
        List<ServerBlockInfo> l= this.file_nodeData.get(file).sbiList;
        Map<NetId, List<Integer>> m= new HashMap<>();

        //We Map each Node to a NodeList
        for (ServerBlockInfo sbi : l)
        {
            m.put(sbi.netId, sbi.get_filesBlocks());
        }
        return m;
    }
}