package ai.prime.agent;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.memory.Memory;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class AgentTests {
    @Test
    public void addData() throws Exception {
        var type = "obj";
        var agent = new Agent("test");
        Memory memory = agent.getMemory();
        memory.addData(new Data(type));

        var all = memory.getAllData();
        var neurons = all.stream().map(data -> memory.getNeuron(data)).collect(Collectors.toList());

        assertTrue(neurons.size() == 1);
        assertTrue(neurons.get(0).getData().getType() == type);
    }


}
