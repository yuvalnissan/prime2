package ai.prime.scenario.experimental.connotation;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Link;
import ai.prime.knowledge.neuron.LinkType;

public class ConnotationLink extends Link {
    public static final LinkType TYPE = new LinkType("connotationLink");

    public ConnotationLink(Data from, Data to, double strength) {
        super(from, to, strength);
    }

    @Override
    public LinkType getType() {
        return TYPE;
    }
}
