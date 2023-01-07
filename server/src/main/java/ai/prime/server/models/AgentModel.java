package ai.prime.server.models;

import java.util.Map;

public class AgentModel {
    private final String name;
    private final Map<String, NeuronModel> memory;

    public AgentModel(String name, Map<String, NeuronModel> memory) {
        this.name = name;
        this.memory = memory;
    }

    public String getName() {
        return name;
    }

    public Map<String, NeuronModel> getMemory() {
        return memory;
    }
}
