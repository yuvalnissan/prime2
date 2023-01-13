package ai.prime.scenario;

import ai.prime.agent.Agent;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.nodes.connotation.IgniteMessage;
import ai.prime.scenario.model.DataModel;
import ai.prime.scenario.model.ScenarioModel;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Scenario {
    private static final double DEFAULT_IGNITE_STRENGTH = 10.0;

    private final String name;
    private final Map<String, Agent> agents;

    public Scenario(String name) {
        this.name = name;
        this.agents = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addAgent(String agentName, Agent agent) {
        this.agents.put(agentName, agent);
    }

    public Agent getAgent(String agentName) {
        return this.agents.get(agentName);
    }

    private static Expression getExpression(DataModel dataModel) {
        Data data;
        if (dataModel.isPrimitive()) {
            data = new ValueData(dataModel.getValue());
        } else {
            Expression[] expressions = dataModel.getExpressions().stream().map(Scenario::getExpression).toArray(Expression[]::new);
            data = new Data(new DataType(dataModel.getType()), expressions);
        }

        DataModifier modifier = dataModel.isNegative() ? DataModifier.NEGATIVE : DataModifier.POSITIVE;

        return new Expression(data, modifier);
    }

    public void igniteNeuron(String agentName, Data data) {
        IgniteMessage igniteMessage = new IgniteMessage(data, data, DEFAULT_IGNITE_STRENGTH);
        Agent agent = getAgent(agentName);
        agent.sendMessageToNeuron(igniteMessage);
    }

    public static Scenario loadScenario(String name) {
        Logger.log("scenarioLoaded", "*** loading scenario: " + name);
        try{
            Scenario scenario = new Scenario(name);
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(Objects.requireNonNull(Scenario.class.getResourceAsStream("/scenarios/" + name + ".json")));
            ScenarioModel scenarioModel = gson.fromJson(reader, ScenarioModel.class);
            List<String> defaultNodes = scenarioModel.getDefaultNodes();
            Map<String, List<String>> nodeMapping = scenarioModel.getNodeMapping();

            scenarioModel.getAgents().forEach((agentName, agentModel) -> {
                Logger.log("scenarioLoaded", "*** loading agent: " + agentName);
                Agent agent = new Agent(agentName);

                defaultNodes.forEach(nodeClassName -> agent.getNodeMapping().registerDefaultNode(nodeClassName));

                nodeMapping.forEach((dataTypeName, nodeClassNames) -> {
                    DataType dataType = new DataType(dataTypeName);
                    nodeClassNames.forEach(nodeClassName -> agent.getNodeMapping().registerDataToNode(dataType, nodeClassName));
                });

                List<DataModel> expressions = agentModel.getExpressions();
                expressions.forEach(expressionModel -> {
                    Expression expression = getExpression(expressionModel);
                    agent.getMemory().addData(expression.getData());
                });

                scenario.addAgent(agentName, agent);
            });

            reader.close();

            return scenario;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
