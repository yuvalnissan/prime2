package ai.prime.scenario;

import ai.prime.agent.Agent;
import ai.prime.agent.interaction.Actuator;
import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.SenseConfidence;
import ai.prime.knowledge.nodes.confidence.SenseMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Environment {

    private final Map<String, Agent> agents;
    private SetMap<String, Data> sent;


    public Environment() {
        this.agents = new HashMap<>();
        this.sent = new SetMap<>();
        reset();
    }

    public abstract Collection<Actuator> getActuators(Agent agent);

    public void registerAgent(Agent agent) {
        agents.put(agent.getName(), agent);
        sendStateToAgent(agent);

        Collection<Actuator> actuators = getActuators(agent);
        actuators.forEach(agent::registerActuator);
    }
    private void sendMessage(Agent agent, Data data, Confidence confidence) {
        SenseMessage message = new SenseMessage(data, data, confidence);
        agent.sendMessageToNeuron(message);
    }

    private void sendMessage(Agent agent, Expression expression) {
        Confidence confidence = expression.getModifier().equals(DataModifier.POSITIVE) ? SenseConfidence.SENSE_POSITIVE : SenseConfidence.SENSE_NEGATIVE;
        sendMessage(agent, expression.getData(), confidence);
        sent.add(agent.getName(), expression.getData());
    }

    private void clear(Agent agent, Set<Data> toClear) {
        toClear.forEach(data -> {
            sendMessage(agent, data, SenseConfidence.SENSE_EMPTY);
            sent.remove(agent.getName(), data);
        });
    }

    private void sendStateToAgent(Agent agent) {
        Set<Expression> toSend = getState();
        Set<Data> sentToAgent = sent.getValues(agent.getName());

        sentToAgent.removeAll(toSend.stream().map(Expression::getData).toList());

        clear(agent, sentToAgent);

        toSend.forEach(expression -> sendMessage(agent, expression));
    }

    public void sendState() {
        agents.values().forEach(this::sendStateToAgent);
    }

    public void reset() {
        agents.values().forEach(agent -> clear(agent, sent.getValues(agent.getName())));
        sent = new SetMap<>();
        innerReset();
        sendState();
    }

    protected abstract Set<Expression> getState();

    public abstract void innerReset();
}
