package ai.prime.knowledge.nodes.binding;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Logger;
import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.Unification;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.ConfidenceNode;
import ai.prime.knowledge.nodes.confidence.ConfidenceUpdateEvent;

import java.util.*;

public class ReceptorNode extends Node {
    public static final String NAME = "receptor";

    private SetMap<Query, Data> queries;
    private SetMap<Query, Data> pendingCheckQueries;
    private Map<Query, Unification> matchingQueries;
    private Set<Query> nonMatchingQueries;

    public ReceptorNode(Neuron neuron) {
        super(neuron);
    }

    private boolean isSelf(Data data) {
        return data.equals(this.getData());
    }

    private boolean isConnected(Data data) {
        return getNeuron().hasLink(ReceptorLink.TYPE, data);
    }

    private void sendQueriesToNeuron(Data target) {
        QueryMessage message = new QueryMessage(this.getData(), target, this.queries.cloneDeep());
        getNeuron().getAgent().sendMessageToNeuron(message);
    }

    private void sendQueries() {
        getNeuron().getLinks(ReceptorLink.TYPE).forEach(link -> sendQueriesToNeuron(link.getTo()));
    }

    private void connectWith(Data data) {
        if (!isConnected(data) && !isSelf(data)) {
            getNeuron().addLink(new ReceptorLink(this.getData(), data));
            ReceptorConnectMessage message = new ReceptorConnectMessage(this.getData(), data);
            getNeuron().getAgent().sendMessageToNeuron(message);
            sendQueriesToNeuron(data);
        }
    }

    @Override
    public void init() {
        queries = new SetMap<>();
        pendingCheckQueries = new SetMap<>();
        matchingQueries = new HashMap<>();
        nonMatchingQueries = new HashSet<>();

        //TODO use connotations instead
        Expression[] expressions = getData().getExpressions();
        for (Expression expression : expressions) {
            connectWith(expression.getData());
        }

        //TODO this is a bit of a hack
        connectWith(new ValueData(getData().getType().getPredicate()));
    }

    @Override
    public String getName() {
        return NAME;
    }

    private boolean isMatch(Query query) {
        if (matchingQueries.containsKey(query)) {
            return true;
        }

        if (nonMatchingQueries.contains(query)) {
            return false;
        }

        if (getData().hasVariables()) {
            return false; //TODO think about it
        }

        if (query.type() == QueryType.PATTERN_MATCH) {
            Unification unification = query.data().unify(getData());
            if (unification != null) {
                Logger.debug("receptorNode", () -> "Neuron " + getData().getDisplayName() + " is matching query: " + query.data().getDisplayName());
                matchingQueries.put(query, unification);
                return true;
            } else {
                nonMatchingQueries.add(query);
                return false;
            }
        }

        throw new RuntimeException("Unknown query type matching: " + query.type());
    }

    private boolean isActive() {
        ConfidenceNode confidenceNode = (ConfidenceNode)getNeuron().getNode(ConfidenceNode.NAME);
        Confidence confidence = confidenceNode.getConfidence();

        return confidence.getStrength() != 0.0;
    }

    private void updateOnMatch(Query query, Data source) {
        Unification binding = matchingQueries.get(query);
        BindingMatch bindingMatch = new BindingMatch(query, source, getData(), binding);
        BindingMessage bindingMessage = new BindingMessage(getData(), source, bindingMatch);
        getNeuron().getAgent().sendMessageToNeuron(bindingMessage);
    }

    private void addQuery(Query query, Data source) {
        queries.add(query, source);

        if (!isActive()) {
            pendingCheckQueries.add(query, source);
            return;
        }
        if (isMatch(query)) {
            updateOnMatch(query, source);
        }
    }

    private void handleConnectMessages(Collection<NeuralMessage> messages) {
        messages.forEach(message -> {
            ReceptorConnectMessage receptorMessage = (ReceptorConnectMessage)message;
            connectWith(receptorMessage.getFrom());
        });
    }

    private void handleQueryMessages(Collection<NeuralMessage> messages) {
        boolean shouldUpdate = false;

        for (NeuralMessage neuralMessage: messages) {
            QueryMessage message = (QueryMessage)neuralMessage;
            var queries = message.getQueries();
            for (Query query: queries.getKeys()) {
                var sources = queries.getValues(query);
                for(Data source: sources) {
                    if (!this.queries.getValues(query).contains(source)) {
                        shouldUpdate = true;
                        addQuery(query, source);
                    }
                }
            }
        }

        if (shouldUpdate) {
            sendQueries();
        }
    }

    private void handleMatchMessage(Collection<NeuralMessage> messages) {
        messages.forEach(neuralMessage -> {
            BindingMessage message = (BindingMessage)neuralMessage;
            Logger.debug("receptorNode", () -> getData().getDisplayName() + " is raising match event");
            getNeuron().addEvent(new BindingEvent(message.getBindingMatch()));
        });
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {
        if (Objects.equals(messageType, ReceptorConnectMessage.TYPE)) {
            handleConnectMessages(messages);
        } else if (Objects.equals(messageType, QueryMessage.TYPE)) {
            handleQueryMessages(messages);
        } else if (Objects.equals(messageType, BindingMessage.TYPE)) {
            handleMatchMessage(messages);
        }
    }

    @Override
    public void handleEvent(NeuralEvent event) {
        if (event.getType().equals(ConfidenceUpdateEvent.TYPE)) {
            if (isActive()) {
                pendingCheckQueries.getKeys().forEach(query -> {
                    if (isMatch(query)) {
                        Set<Data> sources = pendingCheckQueries.getValues(query);
                        sources.forEach(source -> updateOnMatch(query, source));
                    }
                });
                pendingCheckQueries = new SetMap<>();
            }
        }
    }

    public void query(Query query) {
        addQuery(query, getData());
        sendQueries(); //TODO can send a smaller payload
    }

    @Override
    public Map<String, String> getDisplayProps() {
        Map<String, String> props = new HashMap<>();

        return props;
    }
}
