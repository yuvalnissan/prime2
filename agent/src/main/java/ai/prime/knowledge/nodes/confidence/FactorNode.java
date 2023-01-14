package ai.prime.knowledge.nodes.confidence;

import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Logger;
import ai.prime.common.utils.SetMap;
import ai.prime.common.utils.Settings;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;

import java.util.*;
import java.util.stream.Collectors;

public abstract class FactorNode extends Node {
    private static final double RESISTANCE_DECAY = Settings.getDoubleProperty("factor.decay.resistance");
    public static List<String> MESSAGE_TYPES = List.of(new String[]{StatusMessage.TYPE});
    protected static final double RELAX_THRESHOLD = Settings.getDoubleProperty("confidence.relax.threshold");

    private Map<Data, StatusMessage> statusMessages;
    private Set<Data> statusMessageWaitingList;
    private Set<Data> shouldUpdate;


    public FactorNode(Neuron neuron) {
        super(neuron);
    }

    public abstract SetMap<Data, PullValue> buildPullValues();

    private Set<PullValue> adjustPullValues(Set<PullValue> values) {
        return values.stream()
                .map(value -> new PullValue(value.isPositive(),
                        value.getPull(),
                        (1.0 - RESISTANCE_DECAY) * value.getPotentialResistance(),
                        value.getSource()))
                .collect(Collectors.toSet());
    }

    public void sendUpdates() {
        if (statusMessageWaitingList.isEmpty() && !shouldUpdate.isEmpty()) {
            Logger.debug("factorNode", getNeuron().getData().getDisplayName() + " is updating");

            SetMap<Data, PullValue> results = buildPullValues();
            results.getKeys().forEach(to -> {
                Set<PullValue> values = results.getValues(to);
                values = adjustPullValues(values);
                Data normalized = to.normalize();
                PullMessage message = new PullMessage(getNeuron().getData(), normalized, values);
                getNeuron().getAgent().sendMessageToNeuron(message);
                statusMessageWaitingList.add(normalized);
                shouldUpdate.remove(normalized);
            });
        }
    }

    public void sendUpdateOnConnect(Data to) {
        shouldUpdate.add(to.normalize());
        sendUpdates();
    }
    public Confidence getStatusConfidence(Data from) {
        StatusMessage message = statusMessages.get(from);
        if (message != null) {
            return message.getConfidence();
        } else {
            return InferredConfidence.EMPTY;
        }
    }

    @Override
    public void init() {
        statusMessages = new HashMap<>();
        statusMessageWaitingList = new HashSet<>();
        shouldUpdate = new HashSet<>();

        shouldUpdate.add(getNeuron().getData().normalize());

        sendUpdates();
    }

    @Override
    public Collection<String> getMessageTypes() {
        return MESSAGE_TYPES;
    }

    public void setMessage(StatusMessage statusMessage) {
        StatusMessage oldMessage = statusMessages.get(statusMessage.getFrom());
        if (oldMessage == null || statusMessage.getConfidence().isSignificantlyDifferent(oldMessage.getConfidence())) {
            shouldUpdate.add(statusMessage.getFrom().normalize());
        }
        //TODO previously only significant updates happened. Maybe restore
//        shouldUpdate.add(statusMessage.getFrom().normalize());

        statusMessages.put(statusMessage.getFrom(), statusMessage);
        statusMessageWaitingList.remove(statusMessage.getFrom().normalize());
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {
        if (messageType != StatusMessage.TYPE) {
            throw new RuntimeException("Unknown message type");
        }

        messages.forEach(message -> {
            StatusMessage statusMessage = (StatusMessage) message;
            setMessage(statusMessage);
        });

        sendUpdates();
    }
}
