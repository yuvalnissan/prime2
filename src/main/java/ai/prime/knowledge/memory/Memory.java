package ai.prime.knowledge.memory;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;

import java.util.Collection;

public class Memory {

    private Agent agent;
    private NeuralMap neuralMap;

    public Memory(Agent agent) {
        this.agent = agent;
        this.neuralMap = new NeuralMap();
    }

    public void addData(Data data) {
        synchronized (data.getDisplayName()){
            Neuron neuron = new Neuron(agent, data);
            neuralMap.addNeuron(neuron);
        }
    }

    public Neuron getNeuron(Data data){
        synchronized (data.getDisplayName()){
            if (!neuralMap.hasNeuron(data)){
                addData(data);
            }

            return neuralMap.getNeuron(data);
        }
    }

    public Collection<Data> getAllData() {
        return neuralMap.getAllData();
    }
}
