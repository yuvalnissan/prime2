package ai.prime.scenario.experimental.learning;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Logger;
import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.base.VariableData;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.binding.*;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.FactorNode;
import ai.prime.knowledge.nodes.confidence.PullValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EvidenceNode extends FactorNode {
    public static final String NAME = "evidence";

    private Set<Data> conditions;
    private Set<Data> patterns;
    private Evidence evidence;

    public EvidenceNode(Neuron neuron) {
        super(neuron);
    }

    private void sendQuery(Data query) {
        ReceptorNode receptorNode = (ReceptorNode)getNeuron().getNode(ReceptorNode.NAME);
        receptorNode.query(new Query(QueryType.PATTERN_MATCH, query));
    }

    private void updateEvidence() {
        Map<Data, Confidence> conditionConfidence = conditions.stream().collect(
                Collectors.toMap(data -> data, this::getStatusConfidence));
        evidence = new Evidence(getData(), getStatusConfidence(getData()), conditionConfidence);
    }

    @Override
    public void init() {
        this.conditions = new HashSet<>();
        this.patterns = new HashSet<>();

        updateEvidence();

        super.init();

        if (!getData().hasVariables()) {
            sendQuery(new VariableData("X"));
        }
    }

    private void sendUpdateToPattern(Data pattern) {
        var message = new EvidenceUpdateMessage(getData(), pattern, evidence);
        getNeuron().getAgent().sendMessageToNeuron(message);
    }

    @Override
    public SetMap<Data, PullValue> buildPullValues() {
        SetMap<Data, PullValue> results = new SetMap<>();

        if (getData().getType().getPredicate().equals("rule") || getData().getType().getPredicate().equals("infer")) {
            return results;
        }

        results.add(getData(), new PullValue(true, 0.0, 0.0, getData()));
        conditions.forEach(match -> results.add(match, new PullValue(true, 0.0, 0.0, getData())));

        updateEvidence();
        patterns.forEach(this::sendUpdateToPattern);

        return results;
    }

    @Override
    public void handleEvent(NeuralEvent event) {
        if (event.getType().equals(BindingEvent.TYPE)) {
            BindingEvent bindingEvent = (BindingEvent)event;
            BindingMatch bindingMatch = bindingEvent.getMatch();
            if (bindingMatch.getQuery().type() == QueryType.PATTERN_MATCH) {
                Logger.info("evidenceNode", () -> getNeuron().getData() + " got a match from " + bindingMatch.getMatch());

                Data match = bindingMatch.getMatch();
                if (!match.equals(getData()) && !conditions.contains(match)) {
                    conditions.add(match);
                    super.sendUpdateOnConnect(match);
                }
            }
        }
    }

    private void handleConnectWithPatternMessage(PatternConnectMessage message) {
        patterns.add(message.getPattern());
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {
        super.handleMessage(messageType, messages);

        if (messageType.equals(PatternConnectMessage.TYPE)) {
            messages.forEach(message -> handleConnectWithPatternMessage((PatternConnectMessage)message));
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
