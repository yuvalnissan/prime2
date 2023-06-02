package ai.prime.scenario.experimental.learning;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class PatternConnectMessage extends NeuralMessage {
    public static final String TYPE = "patternConnect";

    private final Data pattern;

    public PatternConnectMessage(Data from, Data to, Data pattern) {
        super(from, to);

        this.pattern = pattern;
    }

    public Data getPattern() {
        return pattern;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
