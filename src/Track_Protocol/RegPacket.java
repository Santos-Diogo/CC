package Track_Protocol;

import Shared.Net_Id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegPacket extends TrackPacket
{
    private Map<String, List<Integer>> files_blocks;

    public RegPacket (Net_Id n, TypeMsg type, Map<String, List<Integer>> files_blockss) 
    {
        super (n, type);
        files_blocks = new HashMap<>(files_blockss);
    }

    public Map<String, List<Integer>> get_files_blocks() 
    {
        Map<String, List<Integer>> clonedMap = new HashMap<>();

        for (Map.Entry<String, List<Integer>> entry : this.files_blocks.entrySet()) {
            // Clone the List
	    List<Integer> clonedList = null;
            if(entry.getValue() != null)
		clonedList = new ArrayList<>(entry.getValue());

            clonedMap.put(entry.getKey(), clonedList);
        }

        return clonedMap;
    }

    public void add_file_blocks(String file, List<Integer> blocks) 
    {
        // clone blocks
        List<Integer> l = new ArrayList<>(blocks);
        files_blocks.put(file, l);
    }
}
