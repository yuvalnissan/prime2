package ai.prime.scenario.experimental.actuators;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.agent.interaction.Actuator;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.ConfidenceUpdateEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActuatorNode extends Node {
    public static final String NAME = "actuator";
    public static List<String> MESSAGE_TYPES = List.of(new String[]{});

    private final static double DISCRETE_ACTION_THRESHOLD = 0.5;

    public ActuatorNode(Neuron neuron) {
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
        if (event.getType().equals(ConfidenceUpdateEvent.TYPE)) {
            ConfidenceUpdateEvent confidenceUpdateEvent = (ConfidenceUpdateEvent)event;
            ValueData actionData = (ValueData)getData().getExpressions()[0].getData();
            String action = actionData.getValue();
            Actuator actuator = getNeuron().getAgent().getActuator(action);
            Confidence confidence = confidenceUpdateEvent.getConfidence();
            if (confidence.getStrength() > DISCRETE_ACTION_THRESHOLD) {
                actuator.act(getData());
            } else {
                actuator.cancel(getData());
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Collection<String> getMessageTypes() {
        return MESSAGE_TYPES;
    }

    @Override
    public Map<String, String> getDisplayProps() {
        return new HashMap<>();
    }
}
