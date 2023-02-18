package ai.prime.knowledge.memory;

import ai.prime.common.utils.Lock;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NeuralMap {
    private Map<Data, Neuron> neurons;
    private Lock lock;

    public NeuralMap() {
        this.neurons = new HashMap<>();
        this.lock = new Lock();
    }

    public Neuron getNeuron(Data data) {
        return neurons.get(data);
    }

    public boolean hasNeuron(Data data){
        return neurons.containsKey(data);
    }

    public void addNeuron(Neuron neuron) {
        Data data = neuron.getData();
        lock.lock();
        neurons.put(neuron.getData(), neuron);
        lock.unlock();
    }

    public Collection<Data> getAllData() {
        Collection<Data> all = new HashSet<>();
        lock.lock();
        all.addAll(neurons.keySet()); // cloning
        lock.unlock();

        return all;
    }
}
