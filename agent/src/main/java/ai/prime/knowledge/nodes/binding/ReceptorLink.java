package ai.prime.knowledge.nodes.binding;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Link;
import ai.prime.knowledge.neuron.LinkType;

public class ReceptorLink extends Link {
    public static final LinkType TYPE = new LinkType("receptorLink");

    public ReceptorLink(Data from, Data to) {
        super(from, to);
    }

    @Override
    public LinkType getType() {
        return TYPE;
    }
}
