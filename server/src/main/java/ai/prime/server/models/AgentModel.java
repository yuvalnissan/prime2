package ai.prime.server.models;

import ai.prime.knowledge.neuron.Neuron;

import java.util.HashMap;
import java.util.Map;

public class AgentModel {
    private final String name;
    private final Map<String, NeuronModel> memory;

    public AgentModel(String name) {
        this.name = name;
        this.memory = new HashMap<>();
    }

    public void addNeuron(Neuron neuron) {
        memory.put(neuron.getData().getDisplayName(), new NeuronModel(neuron));
    }

    public String getName() {
        return name;
    }

    public Map<String, NeuronModel> getMemory() {
        return memory;
    }
}
