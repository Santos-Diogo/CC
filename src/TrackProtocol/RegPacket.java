package TrackProtocol;

import Blocker.*;
import Shared.NetId;

public class RegPacket extends TrackPacket
{
    FileBlockInfo fileBlockInfo;

    /**
     * 
     * @param self self NetId
     * @param files_blocks Filename in relation to BlockInfo
     */
    public RegPacket (NetId self, FileBlockInfo files_blocks) 
    {
        super (self, TypeMsg.REG);
        this.fileBlockInfo= files_blocks;
    }

    public BlockInfo get_blockInfo (String file)
    {
        return this.fileBlockInfo.get_blockInfo(file);
    }

    public FileBlockInfo get_fileBlockInfo() 
    {
        return this.fileBlockInfo;
    }
}
