package ai.prime.knowledge.nodes.connotation;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Link;
import ai.prime.knowledge.neuron.LinkType;

public class ConnotationLink extends Link {
    public static final LinkType TYPE = new LinkType("connotationLink");

    private double strength;

    public ConnotationLink(Data from, Data to, double strength) {
        super(from, to);
        this.strength = strength;
    }

    @Override
    public LinkType getType() {
        return TYPE;
    }

    public double getStrength() {
        return strength;
    }
}
