package ai.prime.scenario.experimental.learning;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class EvidenceUpdateMessage extends NeuralMessage {
    public static final String TYPE = "evidenceUpdate";

    private final Evidence evidence;

    public EvidenceUpdateMessage(Data from, Data to, Evidence evidence) {
        super(from, to);

        this.evidence = evidence;
    }

    public Evidence getEvidence() {
        return evidence;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
