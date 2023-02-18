package ai.prime.server;

import ai.prime.agent.Agent;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.nodes.confidence.SenseConfidence;
import ai.prime.scenario.Scenario;
import ai.prime.server.models.AgentModel;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class Controller {
    private final Map<String, Scenario> scenarios;
    private final ModelConversion modelConversion;

    public Controller() {
        scenarios = new HashMap<>();
        modelConversion = new ModelConversion();
    }

    private void allAgentAction(String scenarioName, Consumer<Agent> action) {
        if (!scenarios.containsKey(scenarioName)) {
            return;
        }

        Scenario scenario = scenarios.get(scenarioName);
        scenario.getAllAgents().forEach(action);
    }

    private void stopExistingScenario(String scenarioName) {
        allAgentAction(scenarioName, Agent::stop);
    }

    private void pauseScenario(String scenarioName) {
        allAgentAction(scenarioName, Agent::pause);
    }

    private void resumeScenario(String scenarioName) {
        allAgentAction(scenarioName, Agent::resume);
    }

    private void loadScenario(String scenarioName) {
        stopExistingScenario(scenarioName);
        scenarios.put(scenarioName, Scenario.loadScenario(scenarioName));
    }

    @GetMapping("/scenario")
    public synchronized AgentModel getScenario(@RequestParam(value = "name") String scenarioName, @RequestParam(value = "agent") String agentName){
        if (scenarioName == null) {
            return null;
        }

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
        Expression expression = Expression.fromString(messageBody.getId());

        if (Objects.equals(messageBody.getType(), "ignite")) {
            scenario.igniteNeuron(agentName, expression.getData());
        } else if (Objects.equals(messageBody.getType(), "sense-positive")) {
            scenario.setSense(agentName, expression.getData(), SenseConfidence.SENSE_POSITIVE);
        } else if (Objects.equals(messageBody.getType(), "sense-negative")) {
            scenario.setSense(agentName, expression.getData(), SenseConfidence.SENSE_NEGATIVE);
        } else if (Objects.equals(messageBody.getType(), "add-data")) {
            if (expression.getModifier().equals(DataModifier.POSITIVE)) {
                scenario.setSense(agentName, expression.getData(), SenseConfidence.SENSE_POSITIVE);
            } else {
                scenario.setSense(agentName, expression.getData(), SenseConfidence.SENSE_NEGATIVE);
            }
        } else {
            Logger.error("Invalid scenario message type: " + messageBody.getType());
        }
    }

    @PostMapping("/reset")
    public void resetScenario(@RequestParam(value = "name")  String scenarioName, @RequestBody ScenarioStateBody scenarioStateBody) {
        loadScenario(scenarioName);
    }

    @PostMapping("/pause")
    public void pauseScenario(@RequestParam(value = "name")  String scenarioName, @RequestBody ScenarioStateBody scenarioStateBody) {
        pauseScenario(scenarioName);
    }

    @PostMapping("/resume")
    public void unpauseScenario(@RequestParam(value = "name")  String scenarioName, @RequestBody ScenarioStateBody scenarioStateBody) {
        resumeScenario(scenarioName);
    }
}
