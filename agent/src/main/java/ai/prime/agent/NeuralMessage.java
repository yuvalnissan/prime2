package ai.prime.agent;

import ai.prime.knowledge.data.Data;

public abstract class NeuralMessage {
    private Data from;
    private Data to;

    public NeuralMessage(Data from, Data to) {
        this.from = from;
        this.to = to;
    }

    public Data getFrom() {
        return from;
    }

    public Data getTo() {
        return to;
    }

    abstract String getType();

}
