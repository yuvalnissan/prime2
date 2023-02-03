package ai.prime.scenario.environment.towers;

import ai.prime.agent.Agent;
import ai.prime.agent.interaction.Actuator;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.SenseConfidence;
import ai.prime.knowledge.nodes.confidence.SenseMessage;

import java.util.HashSet;
import java.util.Set;

public class TowerActuator extends Actuator {

    private final Towers towers;
    private final Set<Data> performing;

    public TowerActuator(Agent agent, Towers towers) {
        super(agent);
        this.towers = towers;
        this.performing = new HashSet<>();
    }

    @Override
    public String getMappedAction() {
        return "move";
    }

    private int getIndexFromExpression(Expression expression) {
        var valueData = (ValueData)expression.getData();

        return Integer.parseInt(valueData.getValue().replaceAll("T", ""));
    }

    private void sendActing(Data data, boolean active) {
        Confidence confidence = active ? SenseConfidence.SENSE_POSITIVE : SenseConfidence.SENSE_NEGATIVE;
        Data acting = new Data(new DataType("acting"), data.getExpressions());
        SenseMessage message = new SenseMessage(acting, acting, confidence);
        getAgent().sendMessageToNeuron(message);
    }

    @Override
    public void act(Data data) {
        if (!performing.contains(data)) {
            Logger.debug("towersActuator", "Performing " + data);

            int from = getIndexFromExpression(data.getExpressions()[1]);
            int to = getIndexFromExpression(data.getExpressions()[2]);
            var moved = towers.move(from, to);
            if (moved) {
                sendActing(data, true);
            }
            performing.add(data);
            towers.sendState();
        }
    }

    @Override
    public void cancel(Data data) {
        if (performing.contains(data)) {
            Logger.debug("towersActuator", "Canceling " + data);

            performing.remove(data);
            sendActing(data, false);
        }
    }
}
