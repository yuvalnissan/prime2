package ai.prime.knowledge.nodes;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;

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

    public abstract void handleEvent(NeuralEvent event);

    public abstract String getName();

    public abstract Collection<String> getMessageTypes();

    public abstract Map<String, String> getDisplayProps();
}
