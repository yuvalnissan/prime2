package ai.prime.server;

import ai.prime.agent.Agent;
import ai.prime.server.models.AgentModel;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class Controller {
    private final Map<String, Scenario> scenarios;

    public Controller() {
        scenarios = new HashMap<>();
    }

    @GetMapping("/scenario")
    public AgentModel getAgent(@RequestParam(value = "name") String scenarioName, @RequestParam(value = "agent") String agentName){
        if (!scenarios.containsKey(scenarioName)) {
            scenarios.put(scenarioName, Scenario.loadScenario(scenarioName));
        }
        Scenario scenario = scenarios.get(scenarioName);
        Agent agent = scenario.getAgent(agentName);
        if (agent == null) {
            throw new RuntimeException("Agent " + agentName + " is not in scenario " + scenarioName);
        }
        AgentModel model = new AgentModel(agent.getName());
        agent.getMemory().getAllData().forEach(data -> model.addNeuron(agent.getMemory().getNeuron(data)));

        return model;
    }

//    @PostMapping("/agent")
//    public void postAgent(@RequestParam(value = "name") String name, @RequestBody AgentModel agentModel) throws ServerException {
//        if (agents.containsKey(name)) {
//            throw new ServerException("Agent already exists");
//        }
//        agents.put(name, new Agent(name));
//    }
}
