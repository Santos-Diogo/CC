package Track_Protocol;

import Shared.Net_Id;

/**
 * Temporary implementation
 */
public class GetRepPacket extends TrackPacket
{
    private Net_Id id_node;

    public GetRepPacket (Net_Id self, Net_Id node)
    {
        super (self, TypeMsg.GET_RESP);
        this.id_node= node;
    }

    public Net_Id getNet_Id ()
    {
        return this.id_node;
    }
}