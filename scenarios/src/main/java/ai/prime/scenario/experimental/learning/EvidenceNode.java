package ai.prime.scenario.experimental.learning;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.base.VariableData;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;
import ai.prime.knowledge.nodes.binding.*;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.ConfidenceNode;
import ai.prime.knowledge.nodes.confidence.ConfidenceUpdateEvent;

import java.util.*;

public class EvidenceNode  extends Node {
    public static final String NAME = "evidence";

    private Map<Data, Confidence> confidences;
    private Confidence currentConfidence;
    private Set<Data> matches;

    public EvidenceNode(Neuron neuron) {
        super(neuron);
    }

    private void sendQuery(Data query) {
        ReceptorNode receptorNode = (ReceptorNode)getNeuron().getNode(ReceptorNode.NAME);
        receptorNode.query(new Query(QueryType.PATTERN_MATCH, query));
    }

    @Override
    public void init() {
        this.confidences = new HashMap<>();
        this.matches = new HashSet<>();
        ConfidenceNode confidenceNode = (ConfidenceNode)getNeuron().getNode(ConfidenceNode.NAME);
        currentConfidence = confidenceNode.getConfidence();

        sendQuery(new VariableData("X"));
    }

    private void sendConfidenceUpdate(Data to) {
        EvidenceConfidenceMessage message = new EvidenceConfidenceMessage(getData(), to, getData(), currentConfidence);
        getNeuron().getAgent().sendMessageToNeuron(message);
    }

    private void sendConfidenceUpdates() {
        matches.forEach(this::sendConfidenceUpdate);
    }

    private void handleConfidenceUpdateMessage(EvidenceConfidenceMessage message) {
        confidences.put(message.getChangedData(), message.getConfidence());
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {
        if (Objects.equals(messageType, EvidenceConfidenceMessage.TYPE)) {
            messages.forEach(message -> handleConfidenceUpdateMessage((EvidenceConfidenceMessage)message));
        }
    }

    @Override
    public void handleEvent(NeuralEvent event) {
        if (event.getType().equals(ConfidenceUpdateEvent.TYPE)) {
            ConfidenceUpdateEvent confidenceUpdateEvent = (ConfidenceUpdateEvent)event;
            currentConfidence = confidenceUpdateEvent.getConfidence();
            sendConfidenceUpdates();
        } else if (event.getType().equals(BindingEvent.TYPE)) {
            BindingEvent bindingEvent = (BindingEvent)event;
            BindingMatch bindingMatch = bindingEvent.getMatch();
            if (bindingMatch.getQuery().type() == QueryType.PATTERN_MATCH) {
                Logger.info("evidenceNode", () -> getNeuron().getData() + " got a match from " + bindingMatch.getMatch());

                Data match = bindingMatch.getMatch();
                matches.add(match);
                sendConfidenceUpdate(match);
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, String> getDisplayProps() {
        Map<String, String> props = new HashMap<>();
        confidences.forEach((data, confidence) -> props.put(data.getDisplayName(), confidence.toString()));

        return props;
    }
}
