package ai.prime.scenario.model;

import java.util.List;

public class ScenarioAgentModel {
    private final List<DataModel> expressions;

    public ScenarioAgentModel(List<DataModel> expressions) {
        this.expressions = expressions;
    }

    public List<DataModel> getExpressions() {
        return expressions;
    }
}
