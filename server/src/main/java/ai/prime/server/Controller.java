package ai.prime.server;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.server.models.AgentModel;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class Controller {

    private static final String DEFAULT_AGENT = "default";

    private final Map<String, Agent> agents;

    public Controller() {
        agents = new HashMap<>();
        var agent = new Agent(DEFAULT_AGENT);

        agent.getMemory().addData(new Data(new DataType("rel"), new Expression[]{
                new Expression(new ValueData("Ford")),
                new Expression(new ValueData("isa")),
                new Expression(new ValueData("Car"))
        }));

        agent.getMemory().addData(new Data(new DataType("rel"), new Expression[]{
                new Expression(new ValueData("Ford")),
                new Expression(new ValueData("isa")),
                new Expression(new ValueData("Bird"), DataModifier.NEGATIVE)
        }));

        agent.getMemory().addData(new Data(new DataType("rule"), new Expression[]{
                new Expression(new ValueData("Ford")),
                new Expression(new ValueData("isa")),
                new Expression((new Data(new DataType("rel"), new Expression[]{
                    new Expression(new ValueData("Something")),
                    new Expression(new ValueData("thatIs")),
                    new Expression(new ValueData("Ford"))
                })))
        }));

        agents.put(DEFAULT_AGENT, agent);
    }
    @GetMapping("/agent")
    public AgentModel getAgent(@RequestParam(value = "name") String name) throws ServerException{
        if (!agents.containsKey(name)) {
            throw new ServerException("Missing agent name");
        }

        Agent agent = agents.get(name);
        AgentModel model = new AgentModel(agent.getName());
        agent.getMemory().getAllData().forEach(data -> model.addNeuron(agent.getMemory().getNeuron(data)));

        return model;
    }

    @PostMapping("/agent")
    public void postAgent(@RequestParam(value = "name") String name, @RequestBody AgentModel agentModel) throws ServerException {
        if (agents.containsKey(name)) {
            throw new ServerException("Agent already exists");
        }
        agents.put(name, new Agent(name));
    }
}
