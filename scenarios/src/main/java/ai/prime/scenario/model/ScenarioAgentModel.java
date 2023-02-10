package ai.prime.scenario.model;

import java.util.List;

public class ScenarioAgentModel {
    private final List<NeuronModel> neurons;
    private final List<String> knowledge;

    public ScenarioAgentModel(List<NeuronModel> neurons, List<String> knowledge) {
        this.neurons = neurons;
        this.knowledge = knowledge;
    }

    public List<NeuronModel> getNeurons() {
        return neurons;
    }

    public List<String> getKnowledge() {
        return knowledge;
    }
}
