package ai.prime.knowledge.nodes.confidence;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class SenseMessage extends NeuralMessage {
    public static final String TYPE = "senseMessage";

    private final SenseConfidence confidence;

    public SenseMessage(Data from, Data to, SenseConfidence confidence) {
        super(from, to);
        this.confidence = confidence;
    }

    public SenseConfidence getConfidence() {
        return confidence;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
