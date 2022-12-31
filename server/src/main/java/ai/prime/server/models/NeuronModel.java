package ai.prime.server.models;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.neuron.Neuron;

public class NeuronModel {
    private Data data;

    public NeuronModel(Neuron neuron) {
        this.data = neuron.getData();
    }

    public Data getData() {
        return data;
    }
}
