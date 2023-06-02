package ai.prime.scenario.experimental.learning;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class RuleConnectMessage extends NeuralMessage {
    public static final String TYPE = "ruleConnect";

    private final Data rule;

    public RuleConnectMessage(Data from, Data to, Data rule) {
        super(from, to);

        this.rule = rule;
    }

    public Data getRule() {
        return rule;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
