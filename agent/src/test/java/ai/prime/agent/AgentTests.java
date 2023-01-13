package ai.prime.agent;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.memory.Memory;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class AgentTests {
    private Data getDummyData() {
        return new Data(new DataType("obj"), new Expression[]{new Expression(new ValueData("bla"))});
    }

    @Test
    public void addData() throws Exception {
        var val = "one";
        var agent = new Agent("test");
        Memory memory = agent.getMemory();
        memory.addData(new ValueData(val));

        var all = memory.getAllData();
        var neurons = all.stream().map(data -> memory.getNeuron(data)).collect(Collectors.toList());

        assertTrue(neurons.size() == 1);
        assertTrue(neurons.get(0).getData().getDisplayName() == val);
    }

    @Test
    public void sendMessage() throws Exception {
        var agent = new Agent("test");
        Memory memory = agent.getMemory();
        Data dummy = getDummyData();
        memory.addData(dummy);

        agent.sendMessageToNeuron(new TestMessage(dummy, dummy, "one"));
        agent.sendMessageToNeuron(new TestMessage(dummy, dummy, "two"));

        Thread.sleep(1000);

        agent.sendMessageToNeuron(new TestMessage(dummy, dummy, "three"));
        agent.sendMessageToNeuron(new TestMessage(dummy, dummy, "four"));

        assertTrue(agent.waitForStability(10000));
    }
}
