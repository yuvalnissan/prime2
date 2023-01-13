package ai.prime.server.models;

import java.util.Map;
import java.util.Set;

public class NeuronModel {
    private final DataModel data;
    private final Map<String, NodeModel> nodes;
    private final Set<LinkModel> links;

    public NeuronModel(DataModel data, Map<String, NodeModel> nodes, Set<LinkModel> links) {
        this.data = data;
        this.nodes = nodes;
        this.links = links;
    }

    public DataModel getData() {
        return data;
    }

    public Map<String, NodeModel> getNodes() {
        return nodes;
    }

    public Set<LinkModel> getLinks() {
        return links;
    }
}
