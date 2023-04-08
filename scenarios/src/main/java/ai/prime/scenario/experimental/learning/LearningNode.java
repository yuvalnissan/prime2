package ai.prime.scenario.experimental.learning;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LearningNode extends Node {
    public static final String NAME = "learning";

    private Data pattern;
    private Learner learner;

    public LearningNode(Neuron neuron) {
        super(neuron);
        this.pattern = getData();
        this.learner = new Learner(this.pattern);
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
        return NAME;
    }

    @Override
    public Map<String, String> getDisplayProps() {
                return new HashMap<>();
    }
}
