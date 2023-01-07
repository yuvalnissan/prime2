package ai.prime.server.models;

import java.util.Map;

public class NeuronModel {
    private final DataModel data;
    private final Map<String, NodeModel> nodes;

    public NeuronModel(DataModel data, Map<String, NodeModel> nodes) {
        this.data = data;
        this.nodes = nodes;
    }

    public DataModel getData() {
        return data;
    }

    public Map<String, NodeModel> getNodes() {
        return nodes;
    }
}
