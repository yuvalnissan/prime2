package ai.prime.agent.interaction;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.SenseMessage;

public abstract class Actuator {
    private final Agent agent;

    public Actuator(Agent agent) {
        this.agent = agent;
    }

    public Agent getAgent() {
        return agent;
    }

    public abstract String getMappedAction();

    public abstract void act(Data data, Confidence confidence);

    protected void sendMessage(Data data, Confidence confidence) {
        SenseMessage message = new SenseMessage(data, data, confidence);
        agent.sendMessageToNeuron(message);
    }
}
