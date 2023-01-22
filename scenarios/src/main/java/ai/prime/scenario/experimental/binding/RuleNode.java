package ai.prime.scenario.experimental.binding;

import ai.prime.agent.NeuralEvent;
import ai.prime.common.utils.Logger;
import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.binding.QueryNode;
import ai.prime.knowledge.nodes.binding.ResolvedPatternEvent;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.FactorNode;
import ai.prime.knowledge.nodes.confidence.PullValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RuleNode extends FactorNode {
    public static final String NAME = "rule";

    private static final String INFER_TYPE = "infer"; //TODO maybe find a way to share this, but without class coupling

    private final Expression target;
    private final Expression[] conditions;
    private final Set<Data> matchingInfers;

    public RuleNode(Neuron neuron) {
        super(neuron);
        Expression[] expressions = neuron.getData().getExpressions();
        this.target = expressions[0];
        this.conditions = Arrays.copyOfRange(expressions, 1, expressions.length);
        this.matchingInfers = new HashSet<>();
    }

    @Override
    public String getName() {
        return NAME;
    }

    private Data getTargetPattern() {
        Expression[] expressions = Stream.concat(Arrays.stream(new Expression[]{target}), Arrays.stream(conditions)).toArray(Expression[]::new);

        return new Data(new DataType(INFER_TYPE), expressions);
    }

    @Override
    public void init() {
        super.init();
        QueryNode queryNode = (QueryNode) getNeuron().getNode(QueryNode.NAME);
        queryNode.setBinding(getTargetPattern(), Arrays.stream(this.conditions).map(Expression::getData).collect(Collectors.toList()));
    }

    private void handleMatch(Data resolved) {
        Logger.debug("ruleNode", getData().getDisplayName() + " match found: " + resolved);

        if (!resolved.hasVariables()) {
            matchingInfers.add(resolved);
            super.sendUpdateOnConnect(resolved);
//            Set<PullValue> values = new HashSet<>();
//            values.add(new PullValue(true, 0.0, 0.0, getData()));
//            PullMessage message = new PullMessage(getNeuron().getData(), resolved, values);
//            getNeuron().getAgent().sendMessageToNeuron(message);
        }
    }

    @Override
    public void handleEvent(NeuralEvent event) {
        super.handleEvent(event);

        if (event.getType().equals(ResolvedPatternEvent.TYPE)) {
            handleMatch(((ResolvedPatternEvent)event).getResolved());
        }
    }

    @Override
    public Collection<String> getMessageTypes() {
        return MESSAGE_TYPES;
    }

    private void addDirectionalPull(SetMap<Data, PullValue> results, Data inferData, boolean isPositive) {
        Confidence ruleConfidence = getStatusConfidence(getData());
        Confidence inferConfidence = getStatusConfidence(inferData);

        if (!isPositive) {
            ruleConfidence = ruleConfidence.invert();
            inferConfidence = inferConfidence.invert();
        }

        double conflict = Math.max(0.0, ruleConfidence.getStrength() - inferConfidence.getStrength());
        double pull = conflict * 0.5;

        double ruleResistance = inferConfidence.getResistance(false);
        double inferResistance = ruleConfidence.getResistance(true);

        if (pull <= RELAX_THRESHOLD) {
            pull = 0.0;
            ruleResistance = 0.0; //TODO not sure this is a good idea
            inferResistance = 0.0;
        }
        results.add(getData(), new PullValue(!isPositive, pull, ruleResistance, inferData));
        results.add(inferData, new PullValue(isPositive, pull, inferResistance, getData()));
    }

    private void buildPullValuesForLink(SetMap<Data, PullValue> results, Data inferData) {
        addDirectionalPull(results, inferData, true);
        addDirectionalPull(results, inferData, false);
    }

    @Override
    public SetMap<Data, PullValue> buildPullValues() {
        SetMap<Data, PullValue> results = new SetMap<>();

        matchingInfers.forEach(inferData -> buildPullValuesForLink(results, inferData));

        return results;
    }
}
