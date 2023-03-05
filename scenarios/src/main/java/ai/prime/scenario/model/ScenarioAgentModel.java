package ai.prime.scenario.model;

import java.util.List;
import java.util.Map;

public class ScenarioAgentModel {
    private final Map<String, NeuronModel> neurons;
    private final List<String> knowledge;
    private final List<String> actuators;

    public ScenarioAgentModel(Map<String, NeuronModel> neurons, List<String> knowledge, List<String> actuators) {
        this.neurons = neurons;
        this.knowledge = knowledge;
        this.actuators = actuators;
    }

    public Map<String, NeuronModel> getNeurons() {
        return neurons;
    }

    public List<String> getKnowledge() {
        return knowledge;
    }

    public List<String> getActuators() {
        return actuators;
    }
}
