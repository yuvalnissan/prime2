package ai.prime.knowledge.neuron;

import ai.prime.agent.Agent;
import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class Neuron {
    private Agent agent;
    private Data data;
    private Links links; //TODO deal with links

    public Neuron(Agent agent, Data data){
        this.agent = agent;
        this.data = data;
        this.links = new Links();
    }

    public Agent getAgent() {
        return agent;
    }

    public Data getData() {
        return data;
    }

    public void addMessage(NeuralMessage message) {
        //TODO deal with messages
        System.out.println("Doing nothing with message");
    }

    public void fire() {
        //TODO fire
        System.out.println("firing neuron: " + getData());
    }
}
