package ai.prime.scenario.model;

import java.util.List;
import java.util.Map;

public class ScenarioModel {
    private final Map<String, ScenarioAgentModel> agents;
    private final List<String> defaultNodes;
    private final Map<String, List<String>> nodeMapping;
    private final String environment;

    public ScenarioModel(Map<String, ScenarioAgentModel> agents, List<String> defaultNodes, Map<String, List<String>> nodeMapping, String environment) {
        this.agents = agents;
        this.defaultNodes = defaultNodes;
        this.nodeMapping = nodeMapping;
        this.environment = environment;
    }

    public Map<String, ScenarioAgentModel> getAgents() {
        return agents;
    }

    public List<String> getDefaultNodes() {
        return defaultNodes;
    }

    public Map<String, List<String>> getNodeMapping() {
        return nodeMapping;
    }

    public String getEnvironment() {
        return environment;
    }
}
