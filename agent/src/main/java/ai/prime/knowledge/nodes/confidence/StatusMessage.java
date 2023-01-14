package ai.prime.knowledge.nodes.confidence;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class StatusMessage extends NeuralMessage {
    public static final String TYPE = "statusMessage";

    private final Confidence confidence;

    public StatusMessage(Data from, Data to, Confidence confidence) {
        super(from, to);
        this.confidence = confidence;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return getFrom() + ": " + confidence;
    }

    @Override
    public boolean equals(Object other){
        return toString().equals(other.toString());
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }
}
