package ai.prime.scenario.experimental.learning2;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;

import java.util.Collection;
import java.util.Map;

public class ModelNode extends Node {

    public ModelNode(Neuron neuron) {
        super(neuron);
    }

    @Override
    public void init() {

    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {

    }

    @Override
    public void handleEvent(NeuralEvent event) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, String> getDisplayProps() {
        return null;
    }
}
