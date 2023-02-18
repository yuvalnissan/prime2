package ai.prime.agent.interaction;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.nodes.confidence.Confidence;

import java.util.HashSet;
import java.util.Set;

public abstract class DiscreteActuator extends Actuator {
    private final static double DISCRETE_START_THRESHOLD = 0.7;
    private final static double DISCRETE_STOP_THRESHOLD = 0.2;

    private final Set<Data> performing;

    public DiscreteActuator(Agent agent) {
        super(agent);

        this.performing = new HashSet<>();
    }

    protected abstract void start(Data data);

    protected abstract void stop(Data data);

    @Override
    public void act(Data data, Confidence confidence) {
        if (confidence.getStrength() > DISCRETE_START_THRESHOLD && !performing.contains(data)) {
            performing.add(data);
            start(data);
        }

        if (confidence.getStrength() < DISCRETE_STOP_THRESHOLD && performing.contains(data)) {
            performing.remove(data);
            stop(data);
        }
    }
}
