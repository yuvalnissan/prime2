package ai.prime.server.models;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.neuron.Node;

import java.util.HashMap;
import java.util.Map;

public class NeuronModel {
    private final Data data;
    private final Map<String, NodeModel> nodes;

    public NeuronModel(Neuron neuron) {
        this.data = neuron.getData();
        this.nodes = new HashMap<>();
        neuron.getNodeNames().forEach(nodeName -> {
            Node node = neuron.getNode(nodeName);
            this.nodes.put(nodeName, new NodeModel(node.getDisplayProps())) ;
        });
    }

    public Data getData() {
        return data;
    }

    public Map<String, NodeModel> getNodes() {
        return nodes;
    }
}
