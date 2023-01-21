package ai.prime.knowledge.neuron;

import ai.prime.knowledge.data.Data;

public abstract class Link {
    private Data from;
    private Data to;

    public Link(Data from, Data to) {
        this.from = from;
        this.to = to;
    }

    public abstract LinkType getType();

    public Data getFrom() {
        return from;
    }

    public Data getTo() {
        return to;
    }
}
