package ai.prime.scenario.experimental.inference;

import ai.prime.common.utils.Logger;
import ai.prime.common.utils.SetMap;
import ai.prime.common.utils.Settings;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.FactorNode;
import ai.prime.knowledge.nodes.confidence.InferredConfidence;
import ai.prime.knowledge.nodes.confidence.PullValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ComplexInferenceNode extends FactorNode {
    public static final String NAME = "infer";

    private static final double CONVERGENCE_FACTOR = Settings.getDoubleProperty("factor.convergence.factor");

    private final Expression target;
    private final Expression[] conditions;

    public ComplexInferenceNode(Neuron neuron) {
        super(neuron);
        Expression[] expressions = neuron.getData().getExpressions();
        this.target = expressions[0];
        this.conditions = Arrays.copyOfRange(expressions, 1, expressions.length);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, String> getDisplayProps() {
        Map<String, String> props = new HashMap<>();

        props.put("infer", getStatusConfidence(getData()).toString());
        props.put("target", getStatusConfidence(target.getData()).toString());
        for (Expression condition : conditions) {
            props.put(condition.getData().getDisplayName(), getStatusConfidence(condition.getData()).toString());
        }

        return props;
    }

    private Confidence getConditionConfidence(Expression condition){
        Confidence confidence = getStatusConfidence(condition.getData());
        if (condition.getModifier() == DataModifier.NEGATIVE){
            confidence = confidence.invert();
        }

        return confidence;
    }

    private Confidence getConditionsConfidence() {
        double strength = 1.0;
        double positivePull = Double.MAX_VALUE;
        double negativePull = Double.MAX_VALUE;
        for (Expression condition : conditions) {
            Confidence conditionConfidence = getConditionConfidence(condition);
            strength = Math.min(strength, conditionConfidence.getStrength());
            positivePull = Math.min(positivePull, conditionConfidence.getResistance(true));
            negativePull = Math.min(negativePull, conditionConfidence.getResistance(false));
        }

        return new InferredConfidence(strength, positivePull, negativePull);
    }

    private void addDirectionalCorrelationPull(SetMap<Data, PullValue> results, boolean isPositive) {
        Confidence inferConfidence = getStatusConfidence(getData());

        Confidence conditionConfidence = getConditionsConfidence();
        Confidence targetConfidence = getConditionConfidence(target);

        if (!isPositive) {
            targetConfidence = targetConfidence.invert();
            inferConfidence = inferConfidence.invert();
        }

        double conflict = conditionConfidence.getStrength() - targetConfidence.getStrength();
        double expected = 1.0 - conflict;

        double effectConflict = inferConfidence.getStrength() - expected;
        double pull = Math.max(0.0, effectConflict) * CONVERGENCE_FACTOR;
        if (pull < RELAX_THRESHOLD) {
            pull = 0.0;
        }

        double effectResistance = Math.min(conditionConfidence.getResistance(true), targetConfidence.getResistance(false));
        double fromResistance = Math.min(targetConfidence.getResistance(false), inferConfidence.getResistance(true));
        double toResistance = Math.min(conditionConfidence.getResistance(true), inferConfidence.getResistance(true));

        PullValue inferPullValue = new PullValue(!isPositive, pull, effectResistance, getData());
        results.add(getData(), inferPullValue);
        Logger.debug("inferNode", "\t\t Sent pull " +  getData() + ": " + inferPullValue);


        boolean targetIsPositive = target.getModifier() == DataModifier.NEGATIVE ? !isPositive : isPositive;
        PullValue targetPullValue = new PullValue(targetIsPositive, pull, toResistance, getData());
        results.add(target.getData(), targetPullValue);
        Logger.debug("inferNode", "\t\t Sent pull " +  target.getData() + ": " + targetPullValue);

        for (Expression condition : conditions) {
            //TODO this is not balancing the resistance as the AndNode used to do
            boolean conditionIsPositive = condition.getModifier() == DataModifier.NEGATIVE ? isPositive : !isPositive;
            PullValue conditionPullValue = new PullValue(conditionIsPositive, pull, fromResistance, getData());
            results.add(condition.getData(), conditionPullValue);
            Logger.debug("inferNode", "\t\t Sent pull " +  condition.getData() + ": " + conditionPullValue);
        }
    }

    @Override
    public SetMap<Data, PullValue> buildPullValues() {
        SetMap<Data, PullValue> results = new SetMap<>();

        Logger.debug("inferNode", getNeuron().getData() + " building pull values");
        Logger.debug("inferNode", "\t\t Status " +  getData() + ": " + getStatusConfidence(getData()));
        Logger.debug("inferNode", "\t\t Status " +  target.getData() + ": " + getStatusConfidence(target.getData()));
        for (Expression condition : conditions) {
            Logger.debug("inferNode", "\t\t Status " +  condition.getData() + ": " + getStatusConfidence(condition.getData()));
        }
        Logger.debug("inferNode", "\t\t Status all conditions:" + getConditionsConfidence());

        addDirectionalCorrelationPull(results, true);
        addDirectionalCorrelationPull(results, false);


        return results;
    }
}
