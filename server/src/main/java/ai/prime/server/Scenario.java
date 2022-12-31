package ai.prime.server;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.server.models.DataModel;
import ai.prime.server.models.ScenarioModel;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Scenario {

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

    public static Scenario loadScenario(String name) {
        System.out.println("*** loading scenario: " + name);
        try{
            Scenario scenario = new Scenario(name);
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(Objects.requireNonNull(Scenario.class.getResourceAsStream("/scenarios/" + name + ".json")));
            ScenarioModel scenarioModel = gson.fromJson(reader, ScenarioModel.class);

            scenarioModel.getAgents().forEach((agentName, agentModel) -> {
                System.out.println("*** loading agent: " + agentName);
                Agent agent = new Agent(agentName);
                scenario.addAgent(agentName, agent);

                List<DataModel> expressions = agentModel.getExpressions();
                expressions.forEach(expressionModel -> {
                    Expression expression = getExpression(expressionModel);
                    agent.getMemory().addData(expression.getData());
                });
            });

            reader.close();

            return scenario;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
