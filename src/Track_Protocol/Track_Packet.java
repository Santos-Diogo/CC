package Track_Protocol;

import java.io.Serializable;
import java.net.InetAddress;

public class Track_Packet implements Serializable {
    public enum TypeMsg {
        REG,
        AVF_REQ,
        AVF_REP,
        GET
    }

    private InetAddress src_ip;
    private TypeMsg type;
    private byte[] payload;

    public Track_Packet(InetAddress src_ip, TypeMsg type, byte[] payload) {
        this.src_ip = src_ip;
        this.type = type;
        this.payload = payload;
    }

    public Track_Packet(InetAddress src_ip, TypeMsg type) {
        this.src_ip = src_ip;
        this.type = type;
    }

    public InetAddress getSrc_ip() {
        return src_ip;
    }

    public TypeMsg getType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }

}