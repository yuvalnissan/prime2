package ai.prime.knowledge.memory;

import ai.prime.agent.Agent;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;
import ai.prime.knowledge.nodes.Node;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class Memory {

    private final Agent agent;
    private final NeuralMap neuralMap;

    public Memory(Agent agent) {
        this.agent = agent;
        this.neuralMap = new NeuralMap();
    }

    public void addData(Data data) {
        synchronized (data.getDisplayName()) {
            if (!neuralMap.hasNeuron(data)) {
                Logger.debug("memory", () -> "Adding neuron: " + data.getDisplayName());
                Neuron neuron = new Neuron(agent, data);
                neuralMap.addNeuron(neuron);

                Collection<Class<Node>> nodeClasses = agent.getNodeMapping().getNodesForType(data.getType());
                nodeClasses.forEach(nodeClass -> {
                    try {
                        Constructor<Node> ctor = nodeClass.getConstructor(Neuron.class);
                        Node node = ctor.newInstance(neuron);
                        neuron.addNode(node);
                        node.init();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public Neuron getNeuron(Data data) {
        synchronized (data.getDisplayName()) {
            if (!neuralMap.hasNeuron(data)) {
                addData(data);
            }

            return neuralMap.getNeuron(data);
        }
    }

    public Collection<Data> getAllData() {
        return neuralMap.getAllData();
    }
}
