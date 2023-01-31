package ai.prime.knowledge.nodes.confidence;

import ai.prime.agent.NeuralEvent;

public class ConfidenceUpdateEvent extends NeuralEvent {
    public static final String TYPE = "confidenceUpdated";

    private final Confidence confidence;

    public ConfidenceUpdateEvent(Confidence confidence) {
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
