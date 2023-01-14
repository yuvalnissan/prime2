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
import ai.prime.knowledge.nodes.confidence.PullValue;

import java.util.HashMap;
import java.util.Map;

public class InferNode extends FactorNode {
    public static final String NAME = "infer";

    private static final double CONVERGENCE_FACTOR = Settings.getDoubleProperty("factor.convergence.factor");

    private final Expression condition;
    private final Expression target;

    public InferNode(Neuron neuron) {
        super(neuron);
        this.condition = neuron.getData().getExpressions()[0];
        this.target = neuron.getData().getExpressions()[1];
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, String> getDisplayProps() {
        return new HashMap<>();
    }

    private void addDirectionalCorrelationPull(SetMap<Data, PullValue> results, boolean isPositive) {
        Confidence inferConfidence = getStatusConfidence(getData());

        Confidence conditionConfidence = getStatusConfidence(condition.getData());
        if (condition.getModifier() == DataModifier.NEGATIVE) {
            conditionConfidence = conditionConfidence.invert();
        }

        Confidence targetConfidence = getStatusConfidence(target.getData());
        if (target.getModifier() == DataModifier.NEGATIVE) {
            targetConfidence = targetConfidence.invert();
        }

        if (!isPositive) {
            targetConfidence = targetConfidence.invert();
            inferConfidence = inferConfidence.invert();
        }

        double conflict = conditionConfidence.getStrength() - targetConfidence.getStrength();
        double expected = 1.0 - conflict;

        PullValue inferPullValue;
        PullValue confidencePullValue;
        PullValue targetPullValue;

        boolean conditionIsPositive = condition.getModifier() == DataModifier.NEGATIVE ? isPositive : !isPositive;
        boolean targetIsPositive = target.getModifier() == DataModifier.NEGATIVE ? !isPositive : isPositive;

        double effectConflict = inferConfidence.getStrength() - expected;
        if (effectConflict >= RELAX_THRESHOLD) {
            double pull = Math.max(0.0, effectConflict) * CONVERGENCE_FACTOR;

            double effectResistance = Math.min(conditionConfidence.getResistance(true), targetConfidence.getResistance(false));
            double fromResistance = Math.min(targetConfidence.getResistance(false), inferConfidence.getResistance(true));
            double toResistance = Math.min(conditionConfidence.getResistance(true), inferConfidence.getResistance(true));

            inferPullValue = new PullValue(!isPositive, pull, effectResistance, getData());
            confidencePullValue = new PullValue(conditionIsPositive, pull, fromResistance, getData());

            targetPullValue = new PullValue(targetIsPositive, pull, toResistance, getData());
        } else {
            inferPullValue = new PullValue(!isPositive, 0, 0, getData());
            confidencePullValue = new PullValue(conditionIsPositive, 0, 0, getData());
            targetPullValue = new PullValue(targetIsPositive, 0, 0, getData());
        }

        results.add(getData(), inferPullValue);
        results.add(condition.getData(), confidencePullValue);
        results.add(target.getData(), targetPullValue);

        Logger.debug("inferNode", "\t\t Sent pull " +  getData() + ": " + inferPullValue);
        Logger.debug("inferNode", "\t\t Sent pull " +  condition.getData() + ": " + confidencePullValue);
        Logger.debug("inferNode", "\t\t Sent pull " +  target.getData() + ": " + targetPullValue);
    }

    @Override
    public SetMap<Data, PullValue> buildPullValues() {
        SetMap<Data, PullValue> results = new SetMap<>();

        Logger.info("inferNode", getNeuron().getData() + " building pull values");

        Confidence effectConfidence = getStatusConfidence(getData());
        Confidence fromConfidence = getStatusConfidence(condition.getData());
        Confidence toConfidence = getStatusConfidence(target.getData());

        Logger.debug("inferNode", "\t\t Status " +  getData() + ": " + effectConfidence);
        Logger.debug("inferNode", "\t\t Status " +  condition.getData() + ": " + fromConfidence);
        Logger.debug("inferNode", "\t\t Status " +  target.getData() + ": " + toConfidence);

        addDirectionalCorrelationPull(results, true);
        addDirectionalCorrelationPull(results, false);


        return results;
    }
}
