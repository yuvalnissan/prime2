package ai.prime.server;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataModifier;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;
import ai.prime.server.models.*;

import java.util.*;

public class ModelConversion {

    public DataModel getDataModel(Expression expression) {
        boolean isNegative = expression.getModifier() == DataModifier.NEGATIVE;
        Data data = expression.getData();
        if (data.getType() == ValueData.TYPE) {
            ValueData valueData = (ValueData)data;

            return new DataModel(isNegative, valueData.getDisplayName(), ValueData.TYPE.getPredicate(), valueData.getValue(), null);
        }

        String type = data.getType().getPredicate();
        List<DataModel> expressions = Arrays.stream(data.getExpressions()).toList().stream().map(this::getDataModel).toList();

        return new DataModel(isNegative, data.getDisplayName(), type, null, expressions);
    }

    public DataModel getDataModel(Data data) {
        return getDataModel(new Expression(data));
    }

    public Expression getExpressionFromModel(DataModel dataModel) {
        Data data;
        if (dataModel.getValue() != null) {
            data = new ValueData(dataModel.getValue());
        } else {
            Expression[] expressions = dataModel.getExpressions().stream().map(this::getExpressionFromModel).toArray(Expression[]::new);
            data = new Data(new DataType(dataModel.getType()), expressions);
        }

        DataModifier modifier = dataModel.isNegative() ? DataModifier.NEGATIVE : DataModifier.POSITIVE;

        return new Expression(data, modifier);
    }

    private Map<String, NodeModel> getNodeModels(Neuron neuron) {
        Map<String, NodeModel> nodeModels = new HashMap<>();

        neuron.getNodeNames().forEach(nodeName -> {
            Node node = neuron.getNode(nodeName);
            nodeModels.put(nodeName, new NodeModel(node.getDisplayProps())) ;
        });

        return nodeModels;
    }

    private Set<LinkModel> getLinkModels(Neuron neuron) {
        var linkModels = new HashSet<LinkModel>();
        neuron.getLinkTypes().forEach(type -> {
            var links = neuron.getLinks(type);
            links.forEach(link -> {
                var fromModel = getDataModel(link.getFrom());
                var toModel = getDataModel(link.getTo());
                var linkModel = new LinkModel(type.getName(), fromModel, toModel);
                linkModels.add(linkModel);
            });
        });

        return linkModels;
    }

    public NeuronModel getNeuronModel(Neuron neuron) {
        var dataModel = getDataModel(neuron.getData());
        var nodeModels = getNodeModels(neuron);
        var linkModels = getLinkModels(neuron);

        return new NeuronModel(dataModel, nodeModels, linkModels);
    }

    public AgentModel getAgentModel(Agent agent) {
        Map<String, NeuronModel> memory = new HashMap<>();
        agent.getMemory().getAllData().forEach(data -> {
            Neuron neuron = agent.getMemory().getNeuron(data);
            NeuronModel neuronModel = getNeuronModel(neuron);
            memory.put(neuron.getData().getDisplayName(), neuronModel);
        });

        AgentModel agentModel = new AgentModel(agent.getName(), agent.isStable(), memory, agent.getMessageCount());

        return agentModel;
    }

}
