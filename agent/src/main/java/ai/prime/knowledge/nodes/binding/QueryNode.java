package ai.prime.knowledge.nodes.binding;

import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Logger;
import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.Unification;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;

import java.util.*;

public class QueryNode extends Node {
    public static final String NAME = "query";

    private Data target;
    private List<Data> binders;
    private BindingPath rootBindingPath;
    private Set<Data> sentQueries;
    private SetMap<Data, Unification> matches;
    private SetMap<Data, BindingPath> bindingPaths;

    public QueryNode(Neuron neuron) {
        super(neuron);
    }

    @Override
    public void init() {
        sentQueries = new HashSet<>();
    }

    private void sendQuery(Data query) {
        ReceptorNode receptorNode = (ReceptorNode)getNeuron().getNode(ReceptorNode.NAME);
        receptorNode.query(new Query(QueryType.PATTERN_MATCH, query));
        sentQueries.add(query);
    }

    private boolean wasQuerySent(Data query) {
        return sentQueries.contains(query);
    }

    private void resolvePattern(BindingPath bindingPath) {
        Unification unification = bindingPath.getUnification();
        Data pattern = target;
        Data bound = pattern.bind(unification, true);
        if (bound != null) {
            Logger.info("queryNode", () -> getData().getDisplayName() + " resolved pattern: " + bound);
            getNeuron().addEvent(new ResolvedPatternEvent(bound.normalize()));
        }
    }

    private void setBindingMatch(BindingPath bindingPath, Unification unification) {
        Unification mergedUnification = bindingPath.getUnification().unify(unification);
        if (mergedUnification != null) {
            List<Data> remainingBinders = bindingPath.getRemainingBinders();

            BindingPath next = new BindingPath(remainingBinders, mergedUnification);
            handleBindingPath(next);
        }
    }

    private void setBindingMatch(Data query, Unification unification) {
        Set<BindingPath> queryBindingPaths = this.bindingPaths.getValues(query);
        for (BindingPath bindingPath : queryBindingPaths) {
            setBindingMatch(bindingPath, unification);
        }
    }

    private void handleBindingPath(BindingPath bindingPath) {
        if (!bindingPath.hasMore()) {
            resolvePattern(bindingPath);
        } else {
            Data currentBinder = bindingPath.getCurrentBinder();

            bindingPaths.add(currentBinder, bindingPath);
            if (!wasQuerySent(currentBinder)) {
                sendQuery(currentBinder);
            } else {
                for (Unification unification : matches.getValues(currentBinder)) {
                    setBindingMatch(bindingPath, unification);
                }
            }
        }
    }

    public void setBinding(Data target, List<Data> binders) {
        this.target = target;
        this.binders = binders;
        this.rootBindingPath = new BindingPath(binders);
        this.matches = new SetMap<>();
        this.bindingPaths = new SetMap<>();

        handleBindingPath(rootBindingPath);
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {

    }

    @Override
    public void handleEvent(NeuralEvent event) {
        if (event.getType().equals(BindingEvent.TYPE)) {
            BindingEvent bindingEvent = (BindingEvent)event;
            BindingMatch bindingMatch = bindingEvent.getMatch();
            if (bindingMatch.getQuery().type() == QueryType.PATTERN_MATCH) {
                Logger.info("queryNode", () -> getNeuron().getData() + " got a match from " + bindingMatch.getMatch());

                matches.add(bindingMatch.getQuery().data(), bindingMatch.getBinding());
                setBindingMatch(bindingMatch.getQuery().data(), bindingMatch.getBinding());
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
        bindingPaths.getKeys().forEach(data -> props.put(data.toString(), bindingPaths.getValues(data).toString()));

        return props;
    }
}
