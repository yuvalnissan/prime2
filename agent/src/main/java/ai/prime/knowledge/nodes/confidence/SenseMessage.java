package ai.prime.knowledge.nodes.confidence;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class SenseMessage extends NeuralMessage {
    public static final String TYPE = "senseMessage";

    private final Confidence confidence;

    public SenseMessage(Data to, Confidence confidence) {
        super(to, to);
        this.confidence = confidence;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
