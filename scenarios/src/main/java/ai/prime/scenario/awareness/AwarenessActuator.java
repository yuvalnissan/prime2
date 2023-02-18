package ai.prime.scenario.awareness;

import ai.prime.agent.Agent;
import ai.prime.agent.interaction.DiscreteActuator;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.nodes.confidence.SenseConfidence;
import ai.prime.knowledge.nodes.confidence.SenseMessage;

import java.util.LinkedList;
import java.util.List;

public class AwarenessActuator extends DiscreteActuator {
    private static final Expression start = new Expression(new ValueData("start"));

    private final Agent agent;
    private final List<Expression> happened;

    public AwarenessActuator(Agent agent) {
        super(agent);

        this.agent = agent;
        this.happened = new LinkedList<>();
        this.happened.add(0, start);
    }

    @Override
    public String getMappedAction() {
        return "now";
    }

    private void sendMessage(Agent agent, Data data) {
        SenseMessage message = new SenseMessage(data, data, SenseConfidence.SENSE_POSITIVE);
        agent.sendMessageToNeuron(message);
    }

    private void addSense(Expression last, Expression what) {
        Data was = new Data(new DataType("was"), new Expression[]{what});
        Data wasAfter = new Data(new DataType("wasAfter"), new Expression[]{what, last});

        sendMessage(agent, was);
        sendMessage(agent, wasAfter);
    }

    @Override
    protected void start(Data data) {
        var what = data.getExpressions()[1];
        Logger.debug("awarenessActuator", "Starting " + what.getDisplayName());
        var last = happened.get(0);
        happened.add(0, what);

        addSense(last, what);
    }

    @Override
    protected void stop(Data data) {

    }
}
