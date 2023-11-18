package Payload.TrackPacketPayload;

import java.io.Serializable;
import java.util.*;

public class AvfRepPacket implements TrackPacketPayload, Serializable {
    List<String> files;

    public AvfRepPacket(List<String> files) {
        this.files = new ArrayList<>(files);
    }

    public List<String> get_files() {
        return new ArrayList<>(this.files);
    }
}
