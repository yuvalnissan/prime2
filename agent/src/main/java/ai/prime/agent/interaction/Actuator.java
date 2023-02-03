package ai.prime.agent.interaction;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Data;

public abstract class Actuator {
    private Agent agent;

    public Actuator(Agent agent) {
        this.agent = agent;
    }

    public Agent getAgent() {
        return agent;
    }

    public abstract String getMappedAction();

    public abstract void act(Data data);

    public abstract void cancel(Data data);
}
