package Track_Protocol;

import java.io.Serializable;
import java.net.InetAddress;

public class TrackPacket implements Serializable 
{
    public enum TypeMsg 
    {
        REG,        //Register
        AVF_REQ,    //Available Files Request
        AVF_RESP,   //Available Files Response
        ADD_F,      //Add a File/ Blocks
        RM_F,       //Remove a File/ Blocks
        GET_REQ,    //Get files request
        GET_RESP    //Get files response
    }

    private InetAddress src_ip;
    private TypeMsg type;

    public TrackPacket(InetAddress src_ip, TypeMsg type) 
    {
        this.src_ip= src_ip;
        this.type= type;
    }

    public InetAddress getSrc_ip() 
    {
        return src_ip;
    }

    public TypeMsg getType ()
    {
        return type;
    }
}