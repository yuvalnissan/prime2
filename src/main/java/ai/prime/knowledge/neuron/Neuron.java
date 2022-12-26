package ai.prime.knowledge.neuron;

import ai.prime.agent.Agent;
import ai.prime.knowledge.data.Data;

public class Neuron {

    private Agent agent;
    private Data data;

    public Neuron(Agent agent, Data data){
        this.agent = agent;
        this.data = data;
    }

    public Agent getAgent() {
        return agent;
    }

    public Data getData() {
        return data;
    }
}
