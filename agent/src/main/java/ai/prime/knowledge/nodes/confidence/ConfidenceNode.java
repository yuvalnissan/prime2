package ai.prime.knowledge.nodes.confidence;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Logger;
import ai.prime.common.utils.Settings;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;

import java.util.*;

public class ConfidenceNode extends Node {
    public static final String NAME = "confidence";
    public static List<String> MESSAGE_TYPES = List.of(new String[]{PullMessage.TYPE, SenseMessage.TYPE});
    private static final double CONVERGENCE_FACTOR_STRENGTH = Settings.getDoubleProperty("confidence.convergence.factor.strength");
    private static final double CONVERGENCE_FACTOR_RESISTANCE = Settings.getDoubleProperty("confidence.convergence.factor.resistance");

    private Confidence confidence;
    private boolean isSense;
    private Map<Data,PullMessage> pullMessages;
    private Set<Data> newSources;
    private Set<Data> pullMessageWaitingList;
    private Map<Data, Confidence> lastSentConfidence;

    public ConfidenceNode(Neuron neuron) {
        super(neuron);
    }


    @Override
    public void init() {
        this.confidence = InferredConfidence.EMPTY;
        this.isSense = false;
        this.pullMessages = new HashMap<>();
        this.newSources = new HashSet<>();
        this.pullMessageWaitingList = new HashSet<>();
        this.lastSentConfidence = new HashMap<>();
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
        Map<String, String> props = new HashMap<>();
        props.put("confidence", confidence.toString());
        this.pullMessages.forEach((data, pullMessage) -> {
            props.put(data.getDisplayName(), pullMessage.toString());
        });

        return props;
    }

    private void sendStatus(Data to) {
        StatusMessage message = new StatusMessage(getNeuron().getData(), to, confidence);
        getNeuron().getAgent().sendMessageToNeuron(message);
        lastSentConfidence.put(to, confidence);
    }

    private void processPullMessage(Collection<NeuralMessage> messages) {
        messages.forEach(message -> {
            PullMessage pullMessage = (PullMessage) message;
            Logger.debug("confidenceNode", "Pull message: " + pullMessage);
            pullMessages.put(pullMessage.getFrom(), pullMessage);
            newSources.add(message.getFrom());
            pullMessageWaitingList.remove(message.getFrom());
        });
    }

    private void processSenseMessage(Collection<NeuralMessage> messages) {
        messages.forEach(message -> {
            SenseMessage senseMessage = (SenseMessage) message;
            Confidence oldConfidence = confidence;
            confidence = senseMessage.getConfidence();
            isSense = !confidence.equals(SenseConfidence.SENSE_EMPTY);

            if (confidence.isSignificantlyDifferent(oldConfidence)){
                Logger.info("confidenceNode", "Neuron " + getNeuron().getData().getDisplayName() + " set as sense: " + confidence.toString());

                for (Data source : pullMessages.keySet()){
                    sendStatus(source);
                    pullMessageWaitingList.add(source);
                }

                getNeuron().addEvent(new ConfidenceUpdateEvent(confidence));
            }
        });
    }

    private double getBalancedChange() {
        double changeSum = 0.0;
        double factor = 0.0;

        for (PullMessage message : pullMessages.values()) {
            for (PullValue pullValue : message.getPullValues()) {
                double resistance = pullValue.getPotentialResistance();
                double delta = resistance * pullValue.getPull();
                if (!pullValue.isPositive()) {
                    delta = (-1.0) * delta;
                }
                changeSum += delta;
                factor += resistance;
            }
        }

        changeSum = Math.min(changeSum, SenseConfidence.SENSE_PULL);
        factor = Math.min(factor, SenseConfidence.SENSE_PULL);

        double balancedChange = 0.0;
        if (factor > 0.0){
            balancedChange = changeSum / factor;
        }

        return balancedChange;
    }

    private Confidence getUpdatedConfidence(double balancedChange) {
        double maxPositiveResistance = 0;
        double maxNegativeResistance = 0;

        for (PullMessage message : pullMessages.values()) {
            for (PullValue pullValue : message.getPullValues()) {
                double messagePotentialResistance = pullValue.getPotentialResistance();
                if (pullValue.isPositive()) {
                    maxPositiveResistance = Math.max(maxPositiveResistance, messagePotentialResistance);
                } else {
                    maxNegativeResistance = Math.max(maxNegativeResistance, messagePotentialResistance);
                }
            }
        }

        double oldConfidence = confidence.getStrength();
        double updatedConfidenceValue = (balancedChange * CONVERGENCE_FACTOR_STRENGTH) + oldConfidence;

        double positiveResistance = confidence.getResistance(true) + CONVERGENCE_FACTOR_RESISTANCE * (maxPositiveResistance - confidence.getResistance(true));
        double negativeResistance = confidence.getResistance(false) + CONVERGENCE_FACTOR_RESISTANCE * (maxNegativeResistance - confidence.getResistance(false));

        return new InferredConfidence(updatedConfidenceValue, positiveResistance, negativeResistance);
    }

    private Confidence lowPullAdjustment(Confidence confidence) {
        if (confidence.getStrength() != 0.0 &&
                confidence.getResistance(true) == 0.0 &&
                confidence.getResistance(false) == 0.0) {

            return InferredConfidence.EMPTY;
        } else {
            return confidence;
        }
    }

    private void update() {
        if (!pullMessageWaitingList.isEmpty()) {
            return;
        }

        Confidence current = confidence;

        if (!isSense) {
            double balancedChange = getBalancedChange();
            confidence = getUpdatedConfidence(balancedChange);

            // Resetting if no support
            confidence = lowPullAdjustment(confidence);

            if (confidence.isSignificantlyDifferent(current)) {
                Logger.info("confidenceNode", "Neuron " + getNeuron().getData().getDisplayName() + " changed from " + current.toString() + " to: " + confidence.toString());
                for (PullMessage message : pullMessages.values()){
                    Logger.debug("confidenceNode", "\tPull " + message.getFrom() + ": " + message.getPullValues());
                }

                getNeuron().addEvent(new ConfidenceUpdateEvent(confidence));
            }
        }

        for (Data source : pullMessages.keySet()) {
            Confidence lastSentToNeuron = lastSentConfidence.get(source);
            if (lastSentToNeuron == null || confidence.isSignificantlyDifferent(lastSentToNeuron)) {

                sendStatus(source);
                pullMessageWaitingList.add(source);
            } else if (newSources.contains(source)) {
                sendStatus(source);
            }
        }

        //Resetting the new sources
        newSources = new HashSet<>();
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {
        if (Objects.equals(messageType, PullMessage.TYPE)) {
            processPullMessage(messages);
        } else if (Objects.equals(messageType, SenseMessage.TYPE)) {
            processSenseMessage(messages);
        } else {
            throw new RuntimeException("Wrong message type " + messageType);
        }

        update();
    }

    @Override
    public void handleEvent(NeuralEvent event) {

    }
}
