package ai.prime.scenario.environment.towers;

import ai.prime.agent.Agent;
import ai.prime.agent.interaction.DiscreteActuator;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.SenseConfidence;
import ai.prime.knowledge.nodes.confidence.SenseMessage;

public class TowerActuator extends DiscreteActuator {
    private final Towers towers;

    public TowerActuator(Agent agent, Towers towers) {
        super(agent);
        this.towers = towers;
    }

    @Override
    public String getMappedAction() {
        return "move";
    }

    private int getIndexFromExpression(Expression expression) {
        var valueData = (ValueData)expression.getData();

        return Integer.parseInt(valueData.getValue().replaceAll("t", ""));
    }

    private void sendActing(Data data, boolean active) {
        Confidence confidence = active ? SenseConfidence.SENSE_POSITIVE : SenseConfidence.SENSE_NEGATIVE;
        Data acting = new Data(new DataType("acting"), data.getExpressions());
        SenseMessage message = new SenseMessage(acting, acting, confidence);
        getAgent().sendMessageToNeuron(message);
    }

    private void sendCan(Data data, boolean active) {
        Confidence confidence = active ? SenseConfidence.SENSE_POSITIVE : SenseConfidence.SENSE_NEGATIVE;
        Data acting = new Data(new DataType("canDo"), new Expression[]{new Expression(data)});
        SenseMessage message = new SenseMessage(acting, acting, confidence);
        getAgent().sendMessageToNeuron(message);
    }

    @Override
    protected void start(Data data) {
        Logger.debug("towersActuator", () -> "Performing " + data);

        int from = getIndexFromExpression(data.getExpressions()[1]);
        int to = getIndexFromExpression(data.getExpressions()[2]);
        var moved = towers.move(from, to);
        if (moved) {
            sendActing(data, true);
        } else {
            sendCan(data, false);
        }

        towers.sendState();
    }

    @Override
    protected void stop(Data data) {
        Logger.debug("towersActuator", () -> "Canceling " + data);

        sendActing(data, false);
    }
}
