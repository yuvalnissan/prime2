package ai.prime.knowledge.nodes.confidence;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

import java.util.List;

public class PullMessage extends NeuralMessage {
    public static final String TYPE = "pullMessage";

    private final List<PullValue> pullValues;

    public PullMessage(Data from, Data to, List<PullValue> pullValues) {
        super(from, to);
        this.pullValues = pullValues;
    }

    public List<PullValue> getPullValues() {
        return pullValues;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString(){
        return super.toString() + " " + getPullValues().toString();
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
