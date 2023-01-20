package ai.prime.scenario.model;

import java.util.List;

public class ScenarioAgentModel {
    private final List<NeuronModel> neurons;

    public ScenarioAgentModel(List<NeuronModel> neurons) {
        this.neurons = neurons;
    }

    public List<NeuronModel> getNeurons() {
        return neurons;
    }
}
