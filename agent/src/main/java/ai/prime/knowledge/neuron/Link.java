package ai.prime.knowledge.neuron;

import ai.prime.knowledge.data.Data;

public abstract class Link {
    private Data from;
    private Data to;
    private double strength;

    public Link(Data from, Data to, double strength) {
        this.from = from;
        this.to = to;
        this.strength = strength;
    }

    public abstract LinkType getType();

    public Data getFrom() {
        return from;
    }

    public Data getTo() {
        return to;
    }

    public double getStrength() {
        return strength;
    }
}
