package ai.prime.scenario;

import ai.prime.agent.Agent;
import ai.prime.agent.interaction.Actuator;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.InferredConfidence;
import ai.prime.knowledge.nodes.confidence.SenseConfidence;
import ai.prime.knowledge.nodes.confidence.SenseMessage;
import ai.prime.knowledge.nodes.connotation.IgniteMessage;
import ai.prime.scenario.model.DataModel;
import ai.prime.scenario.model.KnowledgeModel;
import ai.prime.scenario.model.NeuronModel;
import ai.prime.scenario.model.ScenarioModel;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.*;

public class Scenario {
    private static final double DEFAULT_IGNITE_STRENGTH = 10.0;

    private final String name;
    private final Map<String, Agent> agents;
    private Environment environment;

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

    public void setEnvironment(Environment environment) {
        this.environment = environment;

        //Register actuators for all agents
    }

    public Agent getAgent(String agentName) {
        return this.agents.get(agentName);
    }

    public Collection<Agent> getAllAgents() {
        return this.agents.values();
    }

    public Environment getEnvironment() {
        return environment;
    }

    private static Expression getExpression(DataModel dataModel) {
        if (dataModel.hasExpression()) {
            return Expression.fromString(dataModel.getExpression());
        }

        Data data;
        if (dataModel.getValue() != null) {
            data = new ValueData(dataModel.getValue());
        } else if (dataModel.getVar() != null) {
            data = Expression.fromString("var:" + dataModel.getVar()).getData();
        } else {
            Expression[] expressions = dataModel.getExpressions().stream().map(Scenario::getExpression).toArray(Expression[]::new);
            data = new Data(new DataType(dataModel.getType()), expressions);
        }

        DataModifier modifier = dataModel.isNegative() ? DataModifier.NEGATIVE : DataModifier.POSITIVE;

        return new Expression(data, modifier);
    }

    private static Confidence getConfidence(String confidenceStr) {
        if (confidenceStr.equals("POSITIVE")) {
            return SenseConfidence.SENSE_POSITIVE;
        }

        if (confidenceStr.equals("NEGATIVE")) {
            return SenseConfidence.SENSE_NEGATIVE;
        }

        String[] parts = confidenceStr.split("\\|");

        return new InferredConfidence(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }

    public void addDataOnLoad(String agentName, Data data) {
        Agent agent = getAgent(agentName);
        agent.getMemory().addData(data);
    }

    public void igniteNeuron(String agentName, Data data) {
        IgniteMessage igniteMessage = new IgniteMessage(data, data, DEFAULT_IGNITE_STRENGTH);
        Agent agent = getAgent(agentName);
        agent.sendMessageToNeuron(igniteMessage);
    }

    public void setSense(String agentName, Data data, Confidence confidence) {
        SenseMessage message = new SenseMessage(data, confidence);
        Agent agent = getAgent(agentName);
        agent.sendMessageToNeuron(message);
    }

    public void resetEnvironment() {
        environment.reset();
    }

    private static Environment loadEnvironment(String className) {
        try {
            Class<Environment> clazz = (Class<Environment>) Class.forName(className);
            Constructor<Environment> ctor = clazz.getConstructor();

            return ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Actuator loadActuator(String className, Agent agent) {
        try {
            Class<Actuator> clazz = (Class<Actuator>) Class.forName(className);
            Constructor<Actuator> ctor = clazz.getConstructor(Agent.class);

            return ctor.newInstance(agent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ScenarioModel readScenarioModel(String name) {
        try {
            Gson gson = new Gson();

            String root = System.getenv("SCENARIOS");
            Logger.debug("scenario", () -> "root: " + root);
            Reader reader = root != null ?
                    new FileReader(root + "/" + name + ".json") :
                    new InputStreamReader(Objects.requireNonNull(Scenario.class.getResourceAsStream("/scenarios/" + name + ".json")));
            ScenarioModel scenarioModel = gson.fromJson(reader, ScenarioModel.class);
            reader.close();

            return scenarioModel;
        } catch (Exception e) {
            Logger.error("Failed loading scenario" + name, e);
            throw new RuntimeException("Failed loading model");
        }
    }

    private static KnowledgeModel readKnowledgePack(String path) {
        try {
            Gson gson = new Gson();

            String root = System.getenv("SCENARIOS");
            Logger.debug("knowledge", () -> "root: " + root);
            Reader reader = root != null ?
                    new FileReader(root + "/" + path) :
                    new InputStreamReader(Objects.requireNonNull(Scenario.class.getResourceAsStream("/scenarios/" + path)));
            KnowledgeModel knowledgeModel = gson.fromJson(reader, KnowledgeModel.class);
            reader.close();

            return knowledgeModel;
        } catch (Exception e) {
            Logger.error("Failed loading knowledge pack" + path, e);
            throw new RuntimeException("Failed loading knowledge");
        }
    }

    private static void updateNeuronModel(Scenario scenario, String agentName, NeuronModel neuronModel) {
        Expression expression = getExpression(neuronModel.getData());
        var normalized = expression.getData().normalize();
        Logger.debug("scenario", () -> "Adding " + normalized.getDisplayName());
        scenario.addDataOnLoad(agentName, normalized);

        if (neuronModel.getConfidence() != null) {
            Confidence confidence = getConfidence(neuronModel.getConfidence());
            Confidence expressionConfidence = expression.getModifier().equals(DataModifier.NEGATIVE) ? confidence.invert() : confidence;
            scenario.setSense(agentName, normalized, expressionConfidence);
        }
    }

    public static Scenario loadScenario(String name) {
        Logger.info("scenario", () -> "*** loading scenario: " + name);
        ScenarioModel scenarioModel = readScenarioModel(name);

        Scenario scenario = new Scenario(name);

        List<String> defaultNodes = scenarioModel.getDefaultNodes();
        Map<String, List<String>> nodeMapping = scenarioModel.getNodeMapping();

        scenarioModel.getAgents().forEach((agentName, agentModel) -> {
            Logger.info("scenarioLoaded", () -> "*** loading agent: " + agentName);
            Agent agent = new Agent(agentName);

            defaultNodes.forEach(nodeClassName -> agent.getNodeMapping().registerDefaultNode(nodeClassName));

            nodeMapping.forEach((dataTypeName, nodeClassNames) -> {
                DataType dataType = new DataType(dataTypeName);
                nodeClassNames.forEach(nodeClassName -> agent.getNodeMapping().registerDataToNode(dataType, nodeClassName));
            });

            scenario.addAgent(agentName, agent);

            Collection<NeuronModel> neurons = agentModel.getNeurons().values();
            neurons.forEach(neuronModel -> updateNeuronModel(scenario, agentName, neuronModel));

            if (agentModel.getKnowledge() != null) {
                agentModel.getKnowledge().forEach(knowledgePack -> {
                    KnowledgeModel knowledge = readKnowledgePack(knowledgePack);
                    knowledge.getNeurons().values().forEach(neuronModel -> updateNeuronModel(scenario, agentName, neuronModel));
                });
            }

            if (agentModel.getActuators() != null) {
                agentModel.getActuators().forEach(actuatorClassName -> {
                    Actuator actuator = loadActuator(actuatorClassName, agent);
                    agent.registerActuator(actuator);
                });
            }
        });

        if (scenarioModel.getEnvironment() != null) {
            Environment environment = loadEnvironment(scenarioModel.getEnvironment());
            scenario.setEnvironment(environment);
            scenario.getAllAgents().forEach(agent -> scenario.getEnvironment().registerAgent(agent));
        }

        return scenario;
    }
}
