package Shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeBlocks {
    
    private Map<NetId, List<Long>> nodeblocks;

    public NodeBlocks (Map<NetId, List<Long>> nodeblocks)
    {
        this.nodeblocks = nodeblocks;
    }

    public List<Tuple<Long, Integer>> rarestBlocks (Long nBlocks)
    {
        Map<Long, Integer> record = new HashMap<>();
        for(long i = 1; i <= nBlocks; i++)
            record.put(i, 0);
        for(List<Long> blocks : nodeblocks.values())
        {
            if(blocks == null)
                for(long i = 1; i <= nBlocks; i++)
                {
                    int tmp = record.get(i);
                    record.put(i, tmp + 1);
                }
            else
                for(Long l : blocks)
                {
                    int tmp = record.get(l);
                    record.put(l, tmp + 1);
                }
        }
        return record.entrySet().stream()
                                .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()))
                                .sorted((entry1, entry2) -> Long.compare(entry1.fst(), entry2.snd()))
                                .collect(Collectors.toList());
        
    }
    
    public NetId get_loneBlock (Long block)
    {
        for(Map.Entry<NetId, List<Long>> entry : nodeblocks.entrySet())
            if (entry.getValue().contains(block))
                return entry.getKey();
        return null;
    }

    public List<NetId> get_nodesBlock (Long block)
    {
        List<NetId> nodes = new ArrayList<>();
        for(Map.Entry<NetId, List<Long>> nb : nodeblocks.entrySet())
            if (nb.getValue().contains(block))
                nodes.add(nb.getKey());
        return nodes;
    }

    public Set<NetId> get_nodes ()
    {
        return this.nodeblocks.keySet();
    }
}
