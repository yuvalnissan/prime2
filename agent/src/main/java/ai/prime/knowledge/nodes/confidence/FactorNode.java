package ai.prime.knowledge.nodes.confidence;

import ai.prime.agent.NeuralEvent;
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
    protected static final double RELAX_THRESHOLD = Settings.getDoubleProperty("confidence.relax.threshold");

    private Map<Data, StatusMessage> statusMessages;
    private Set<Data> statusMessageWaitingList;
    private Set<Data> shouldUpdate;

    //TODO for debug, remove later
    private Map<Data, Set<PullValue>> lastSent;


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
            Logger.info("factorNode", () -> getNeuron().getData().getDisplayName() + " is updating");

            lastSent = new HashMap<>();
            SetMap<Data, PullValue> results = buildPullValues();
            results.getKeys().forEach(to -> {
                Set<PullValue> values = adjustPullValues(results.getValues(to));
                Data normalized = to.normalize();
                PullMessage message = new PullMessage(getNeuron().getData(), normalized, values);
                getNeuron().getAgent().sendMessageToNeuron(message);

                lastSent.put(normalized, values);
                statusMessageWaitingList.add(normalized);
                shouldUpdate.remove(normalized);
            });
        }
    }

    protected Set<PullValue> getLastSent(Data data) {
        if (!lastSent.containsKey(data)) {
            return new HashSet<>();
        }
        return lastSent.get(data);
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
        lastSent = new HashMap<>();

        shouldUpdate.add(getNeuron().getData().normalize());

        sendUpdates();
    }

    public void setMessage(StatusMessage statusMessage) {
        Logger.debug("factorNode", () -> getNeuron().getData().getDisplayName() + " got a status message: " + statusMessage);
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
        if (!messageType.equals(StatusMessage.TYPE)) {
            return;
        }

        messages.forEach(message -> {
            StatusMessage statusMessage = (StatusMessage) message;
            setMessage(statusMessage);
        });

        sendUpdates();
    }

    @Override
    public void handleEvent(NeuralEvent event) {

    }

    @Override
    public Map<String, String> getDisplayProps() {
        Map<String, String> props = new HashMap<>();
        props.put("status", statusMessages.toString());
        return props;
    }
}
