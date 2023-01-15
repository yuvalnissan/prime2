package ai.prime.server.models;

import java.util.Map;

public class AgentModel {
    private final String name;
    private final boolean stable;
    private final Map<String, NeuronModel> memory;
    private final long messageCount;

    public AgentModel(String name, boolean stable, Map<String, NeuronModel> memory, long messageCount) {
        this.name = name;
        this.stable = stable;
        this.memory = memory;
        this.messageCount = messageCount;
    }

    public String getName() {
        return name;
    }

    public boolean isStable() {
        return stable;
    }

    public Map<String, NeuronModel> getMemory() {
        return memory;
    }

    public long getMessageCount() {
        return messageCount;
    }
}
