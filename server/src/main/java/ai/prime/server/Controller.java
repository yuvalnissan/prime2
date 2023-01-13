package ai.prime.server;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Expression;
import ai.prime.scenario.Scenario;
import ai.prime.server.models.AgentModel;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class Controller {
    private final Map<String, Scenario> scenarios;
    private final ModelConversion modelConversion;

    public Controller() {
        scenarios = new HashMap<>();
        modelConversion = new ModelConversion();
    }

    private void loadScenario(String scenarioName) {
        scenarios.put(scenarioName, Scenario.loadScenario(scenarioName));
    }

    @GetMapping("/scenario")
    public AgentModel getScenario(@RequestParam(value = "name") String scenarioName, @RequestParam(value = "agent") String agentName){
        if (!scenarios.containsKey(scenarioName)) {
            loadScenario(scenarioName);
        }
        Scenario scenario = scenarios.get(scenarioName);
        Agent agent = scenario.getAgent(agentName);
        if (agent == null) {
            throw new RuntimeException("Agent " + agentName + " is not in scenario " + scenarioName);
        }
        AgentModel agentModel = modelConversion.getAgentModel(agent);

        return agentModel;
    }

    @PostMapping("/message")
    public void sendMessage(@RequestParam(value = "name") String scenarioName, @RequestParam(value = "agent") String agentName, @RequestBody AgentMessageBody messageBody) {
        Scenario scenario = scenarios.get(scenarioName);
        Expression expression = modelConversion.getExpressionFromModel(messageBody.getData());

        if (Objects.equals(messageBody.getType(), "ignite")) {
            scenario.igniteNeuron(agentName, expression.getData());
        }
    }

    @PostMapping("/reset")
    public void resetScenario(@RequestParam(value = "name")  String scenarioName, @RequestBody ResetScenarioBody resetScenarioBody) {
        loadScenario(scenarioName);
    }
}
