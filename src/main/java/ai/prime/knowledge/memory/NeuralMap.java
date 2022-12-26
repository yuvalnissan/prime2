package ai.prime.knowledge.memory;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;

import java.util.*;

public class NeuralMap {
    private Map<Data, Neuron> neurons;

    public NeuralMap() {
        this.neurons = new HashMap<>();
    }

    public Neuron getNeuron(Data data) {
        return neurons.get(data);
    }

    public boolean hasNeuron(Data data){
        return neurons.containsKey(data);
    }

    public void addNeuron(Neuron neuron) {
        Data data = neuron.getData();
        neurons.put(neuron.getData(), neuron);
    }

    public Collection<Data> getAllData() {
        Collection<Data> all = new HashSet<>();
        all.addAll(neurons.keySet()); // cloning

        return all;
    }
}
