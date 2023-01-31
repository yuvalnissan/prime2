package ai.prime.scenario.environment.towers;

import ai.prime.agent.interaction.Actuator;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;

import java.util.HashSet;
import java.util.Set;

public class TowerActuator implements Actuator {

    private final Towers towers;
    private final Set<Data> performing;

    public TowerActuator(Towers towers) {
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

    @Override
    public void act(Data data) {
        if (!performing.contains(data)) {
            Logger.debug("towersActuator", "Performing " + data);

            int from = getIndexFromExpression(data.getExpressions()[1]);
            int to = getIndexFromExpression(data.getExpressions()[2]);
            towers.move(from, to);
            performing.add(data);
            towers.sendState();
        }
    }

    @Override
    public void cancel(Data data) {
        if (performing.contains(data)) {
            Logger.debug("towersActuator", "Canceling " + data);

            performing.remove(data);
        }
    }
}
