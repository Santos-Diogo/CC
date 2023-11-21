package TrackProtocol;

import java.util.Map;

import Shared.NetId;
import Block.BlockInfo;

public class RegPacket extends TrackPacket
{
    private Map<String, BlockInfo> fileBlockInfo;

    /**
     * 
     * @param self self NetId
     * @param files_blocks Filename in relation to BlockInfo
     */
    public RegPacket (NetId self, Map<String, BlockInfo> files_blocks) 
    {
        super (self, TypeMsg.REG);
        this.fileBlockInfo= files_blocks;
    }

    public BlockInfo get_blockInfo (String file)
    {
        return this.fileBlockInfo.get(file);
    }

    public Map<String, BlockInfo> get_fileBlockInfo() 
    {
        return this.fileBlockInfo;
    }
}
