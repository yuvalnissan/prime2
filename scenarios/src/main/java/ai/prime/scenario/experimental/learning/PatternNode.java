package ai.prime.scenario.experimental.learning;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;
import ai.prime.knowledge.nodes.binding.*;

import java.util.*;

public class PatternNode extends Node {
    public static final String NAME = "pattern";

    private Data pattern;
    private Learner learner;
    private Set<Data> matches;

    public PatternNode(Neuron neuron) {
        super(neuron);
        this.pattern = getData().getExpressions()[0].getData();
    }

    private void sendQuery(Data query) {
        ReceptorNode receptorNode = (ReceptorNode)getNeuron().getNode(ReceptorNode.NAME);
        receptorNode.query(new Query(QueryType.PATTERN_MATCH, query));
    }

    private void connectToRule(Data to) {
        var message = new RuleConnectMessage(getData(), to, getData());
        getNeuron().getAgent().sendMessageToNeuron(message);
    }

    private void handleNewRule(Data rule) {
        Logger.debug("patternNode", () -> "Getting the Data " + rule.getDisplayName());
        matches.add(rule);
        connectToRule(rule);
    }

    @Override
    public void init() {
        this.matches = new HashSet<>();
        this.learner = new Learner(this.pattern, this::handleNewRule);

        sendQuery(this.pattern);
    }

    private void handleUpdatedEvidenceMessage(EvidenceUpdateMessage message) {
        learner.addEvidence(message.getEvidence());
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {
        if (messageType.equals(EvidenceUpdateMessage.TYPE)) {
            messages.forEach(message -> handleUpdatedEvidenceMessage((EvidenceUpdateMessage)message));
        }
    }

    private void connectToEvidence(Data to) {
        var message = new PatternConnectMessage(getData(), to, getData());
        getNeuron().getAgent().sendMessageToNeuron(message);
    }

    @Override
    public void handleEvent(NeuralEvent event) {
        if (event.getType().equals(BindingEvent.TYPE)) {
            BindingEvent bindingEvent = (BindingEvent)event;
            BindingMatch bindingMatch = bindingEvent.getMatch();
            if (bindingMatch.getQuery().type() == QueryType.PATTERN_MATCH) {
                Logger.info("patternNode", () -> getNeuron().getData() + " got a match from " + bindingMatch.getMatch());
                connectToEvidence(bindingMatch.getMatch());
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

        return props;
    }
}
