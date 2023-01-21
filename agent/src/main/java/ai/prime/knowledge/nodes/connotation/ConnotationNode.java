package ai.prime.knowledge.nodes.connotation;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;

import java.util.*;


public class ConnotationNode extends Node {
    public static final String NAME = "connotation";
    public static List<String> MESSAGE_TYPES = List.of(new String[]{ConnotationConnectMessage.TYPE, IgniteMessage.TYPE});
    private static final double DEFAULT_LINK_STRENGTH = 0.5;

    private double strength = 0;

    public ConnotationNode(Neuron neuron) {
        super(neuron);
    }

    private boolean isSelf(Data data) {
        return data.equals(this.getData());
    }

    private boolean isConnected(Data data) {
        return getNeuron().hasLink(ConnotationLink.TYPE, data);
    }

    private void connectWith(Data data) {
        if (!isConnected(data) && !isSelf(data)) {
            getNeuron().addLink(new ConnotationLink(this.getData(), data, DEFAULT_LINK_STRENGTH));
            ConnotationConnectMessage message = new ConnotationConnectMessage(this.getData(), data);
            getNeuron().getAgent().sendMessageToNeuron(message);
        }
    }

    public double getStrength() {
        return strength;
    }

    @Override
    public void init() {
        Expression[] expressions = getData().getExpressions();
        for (Expression expression : expressions) {
            connectWith(expression.getData());
        }
    }

    private void handleConnectMessages(Collection<NeuralMessage> messages) {
        messages.forEach(message -> {
            ConnotationConnectMessage connectMessage = (ConnotationConnectMessage)message;
            connectWith(connectMessage.getFrom());
        });
    }

    private void handleIgniteMessages(Collection<NeuralMessage> messages) {
        messages.forEach(message -> {
            IgniteMessage igniteMessage = (IgniteMessage)message;
            strength += igniteMessage.getStrength(); //TODO still doesn't do anything
        });
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {
        if (Objects.equals(messageType, ConnotationConnectMessage.TYPE)) {
            handleConnectMessages(messages);
        } else if (Objects.equals(messageType, IgniteMessage.TYPE)) {
            handleIgniteMessages(messages);
        } else {
            throw new RuntimeException("Wrong message type " + messageType);
        }
    }

    @Override
    public void handleEvent(NeuralEvent event) {

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<String> getMessageTypes() {
        return MESSAGE_TYPES;
    }

    @Override
    public Map<String, String> getDisplayProps() {
        Map<String, String> props = new HashMap<>();
        props.put("strength", String.valueOf(getStrength()));

        return props;
    }
}
