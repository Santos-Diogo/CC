package Network.TCP.TrackProtocol;

import Blocker.*;

public class RegReqPacket extends TrackPacket
{
    FileBlockInfo fileBlockInfo;

    /**
     * 
     * @param self self NetId
     * @param files_blocks Filename in relation to BlockInfo
     */
    public RegReqPacket (TrackPacket track_packet, FileBlockInfo files_blocks) 
    {
        super (track_packet);
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
