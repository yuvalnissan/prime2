package ai.prime.scenario.experimental.exceptions;

import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Logger;
import ai.prime.common.utils.SetMap;
import ai.prime.common.utils.Settings;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.confidence.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ExceptionNode extends FactorNode {
    public static final String NAME = "inferWithExceptions";
    public static List<String> MESSAGE_TYPES = List.of(new String[]{StatusMessage.TYPE, ExceptionMessage.TYPE});
    private static final double CONVERGENCE_FACTOR = Settings.getDoubleProperty("factor.convergence.factor");

    private final Expression target;
    private final Expression[] conditions;
    private final Collection<Expression> overrides;
    private final Map<Data, PullValue> exceptions;

    public ExceptionNode(Neuron neuron) {
        super(neuron);
        Expression[] expressions = neuron.getData().getExpressions();
        this.target = expressions[0];
        this.conditions = Arrays.copyOfRange(expressions, 1, expressions.length);
        this.overrides = getOverrides();
        this.exceptions = new HashMap<>();
    }

    private Collection<Expression> getOverrides() {
        return IntStream.rangeClosed(0, this.conditions.length - 1).mapToObj(count -> {
            Expression[] partialCondition = Arrays.copyOfRange(conditions, 0, count);
            Expression inverseTarget = target.not();

            Expression[] expressions = Stream.concat(Arrays.stream(new Expression[]{inverseTarget}), Arrays.stream(partialCondition)).toArray(Expression[]::new);

            Data partialInfer = new Data(new DataType("infer"), expressions);

            return new Expression(partialInfer);
        }).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Collection<String> getMessageTypes() {
        //TODO this is a nasty hack, need to organize the messages the instance deal vs the super
        return MESSAGE_TYPES;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, String> getDisplayProps() {
        Map<String, String> props = super.getDisplayProps();

        props.put("infer", getStatusConfidence(getData()).toString());
        props.put(target.getData().getDisplayName(), getStatusConfidence(target.getData()).toString());
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
        if (conditions.length == 0) {
            return InferredConfidence.EMPTY;
        }
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

    private Confidence getConditionsWithException() {
        Confidence conditionConfidence = getConditionsConfidence();

        var factor = conditionConfidence.getResistance(true);
        var changeSum = conditionConfidence.getStrength() * factor;
        var maxExceptionResistance = 0.0;
        for (PullValue exceptionPull : exceptions.values()) {
            factor += exceptionPull.getPotentialResistance();
            changeSum -= exceptionPull.getPull() * exceptionPull.getPotentialResistance();
            maxExceptionResistance = Math.max(maxExceptionResistance, exceptionPull.getPotentialResistance());
        }

        var strength = factor > 0.0 ? changeSum / factor : 0.0;
        var positivePull = Math.max(0.0, conditionConfidence.getResistance(true) - maxExceptionResistance);

        return new InferredConfidence(strength, positivePull, 0.0);
    }

    private void addDirectionalCorrelationPull(SetMap<Data, PullValue> results, boolean isPositive) {
        Confidence inferConfidence = getStatusConfidence(getData());

        Confidence conditionConfidence = getConditionsWithException();
        Confidence targetConfidence = getConditionConfidence(target);

        if (!isPositive) {
            targetConfidence = targetConfidence.invert();
            inferConfidence = inferConfidence.invert();
        }

        double conflict = Math.max(0.0, conditionConfidence.getStrength() - targetConfidence.getStrength());
//        double expected = 1.0 - conflict;
//        double effectConflict = inferConfidence.getStrength() - expected;
//        double pull = Math.max(0.0, effectConflict) * CONVERGENCE_FACTOR;

        //TODO test this, different that general logic
        var inferEffect = Math.max(inferConfidence.getStrength(), 0.0);
        double pull = conflict * inferEffect * CONVERGENCE_FACTOR;

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

    private void addExceptionPull(Expression exception) {
        Confidence inferConfidence = getStatusConfidence(getData());
        Confidence conditionConfidence = getConditionsConfidence();

        var pull = Math.max(conditionConfidence.getStrength(), 0.0) * Math.max(inferConfidence.getStrength(), 0.0);
        var resistance = Math.min(conditionConfidence.getResistance(true), inferConfidence.getResistance(true));

        PullValue exceptionPullValue = new PullValue(false, pull, resistance, getData());
        ExceptionMessage exceptionMessage = new ExceptionMessage(getData(), exception.getData(), exceptionPullValue);
        getNeuron().getAgent().sendMessageToNeuron(exceptionMessage);
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
        Logger.debug("exceptionNode", "\t\t Status all conditions with exception:" + getConditionsWithException());

        addDirectionalCorrelationPull(results, true);
        addDirectionalCorrelationPull(results, false);

        overrides.forEach(this::addExceptionPull);

        return results;
    }

    private void handleExceptionMessages(Collection<NeuralMessage> messages) {
        messages.forEach(neuralMessage -> {
            ExceptionMessage exceptionMessage = (ExceptionMessage)neuralMessage;
            exceptions.put(exceptionMessage.getFrom(), exceptionMessage.getExceptionPull());
            Logger.debug("exceptionNode", getData() + " got exception from " + exceptionMessage.getFrom() + ": " + exceptionMessage.getExceptionPull());
        });
    }

    @Override
    public void handleMessage(String messageType, Collection<NeuralMessage> messages) {
        if (messageType.equals(ExceptionMessage.TYPE)) {
            handleExceptionMessages(messages);
            super.sendUpdates();
        }

        if (messageType.equals(StatusMessage.TYPE)) {
            super.handleMessage(messageType, messages);
        }
    }
}
