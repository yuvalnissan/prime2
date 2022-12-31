package ai.prime.server.models;

import java.util.Map;

public class ScenarioModel {
    private final Map<String, ScenarioAgentModel> agents;

    public ScenarioModel(Map<String, ScenarioAgentModel> agents) {
        this.agents = agents;
    }

    public Map<String, ScenarioAgentModel> getAgents() {
        return agents;
    }
}
