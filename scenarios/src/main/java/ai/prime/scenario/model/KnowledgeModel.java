package ai.prime.scenario.model;

import java.util.Map;

public class KnowledgeModel {
    private final Map<String, NeuronModel> neurons;

    public KnowledgeModel(Map<String, NeuronModel> neurons) {
        this.neurons = neurons;
    }

    public Map<String, NeuronModel> getNeurons() {
        return neurons;
    }
}
