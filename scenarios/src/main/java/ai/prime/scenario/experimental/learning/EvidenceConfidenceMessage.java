package ai.prime.scenario.experimental.learning;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.nodes.confidence.Confidence;

public class EvidenceConfidenceMessage extends NeuralMessage {
    public static final String TYPE = "evidenceConfidenceMessage";

    private Data changedData;
    private Confidence confidence;

    public EvidenceConfidenceMessage(Data from, Data to, Data changedData, Confidence confidence) {
        super(from, to);

        this.changedData = changedData;
        this.confidence = confidence;
    }

    public Data getChangedData() {
        return changedData;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
