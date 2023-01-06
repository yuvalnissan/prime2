package ai.prime.knowledge.neuron;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

import java.util.Collection;
import java.util.Map;

public abstract class Node {
    private Neuron neuron;

    public Node(Neuron neuron) {
        this.neuron = neuron;
    }

    public Neuron getNeuron() {
        return neuron;
    }

    public Data getData() {return neuron.getData();}

    public String toString(){
        return getName() + "-" + getNeuron().getData().getDisplayName();
    }

    public abstract void init();

    public abstract void handleMessage(String messageType, Collection<NeuralMessage> messages);

    public abstract String getName();

    public abstract Collection<String> getMessageTypes();

    public abstract Map<String, String> getDisplayProps();
}
