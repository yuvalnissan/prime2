package ai.prime.scenario.model;

import java.util.List;

public class ScenarioAgentModel {
    private final List<NeuronModel> neurons;
    private final List<String> knowledge;
    private final List<String> actuators;

    public ScenarioAgentModel(List<NeuronModel> neurons, List<String> knowledge, List<String> actuators) {
        this.neurons = neurons;
        this.knowledge = knowledge;
        this.actuators = actuators;
    }

    public List<NeuronModel> getNeurons() {
        return neurons;
    }

    public List<String> getKnowledge() {
        return knowledge;
    }

    public List<String> getActuators() {
        return actuators;
    }
}
