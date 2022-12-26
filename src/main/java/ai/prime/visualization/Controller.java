package ai.prime.visualization;

import ai.prime.agent.Agent;
import ai.prime.visualization.models.AgentModel;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Controller {

    private static final String DEFAULT_AGENT = "default";

    private final Map<String, Agent> agents;

    public Controller() {
        agents = new HashMap<>();
        agents.put(DEFAULT_AGENT, new Agent(DEFAULT_AGENT));
    }
    @GetMapping("/agent")
    public AgentModel getAgent(@RequestParam(value = "name") String name) throws ServerException{
        if (!agents.containsKey(name)) {
            throw new ServerException("Missing agent name");
        }

        Agent selected = agents.get(name);

        var response = new AgentModel(selected.getName());

        return response;
    }

    @PostMapping("/agent")
    public void postAgent(@RequestParam(value = "name") String name, @RequestBody AgentModel agentModel) throws ServerException {
        if (agents.containsKey(name)) {
            throw new ServerException("Agent already exists");
        }
        agents.put(name, new Agent(name));
    }
}
