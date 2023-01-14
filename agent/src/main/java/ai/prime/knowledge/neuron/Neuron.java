package ai.prime.knowledge.neuron;

import ai.prime.agent.Agent;
import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.Lock;
import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.nodes.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Neuron {
    private final Agent agent;
    private final Data data;
    private final Links links;
    private final Map<String, Node> nodes;

    private SetMap<String, NeuralMessage> messages;
    private Lock fireLock;
    private Lock messageLock;

    public Neuron(Agent agent, Data data){
        this.agent = agent;
        this.data = data;
        this.links = new Links();
        this.nodes = new HashMap<>();
        this.messages = new SetMap<>();

        this.messageLock = new Lock();
        this.fireLock = new Lock();
    }

    public Agent getAgent() {
        return agent;
    }

    public Data getData() {
        return data;
    }

    public Links getLinks() {
        return links;
    }

    public void addNode(Node node) {
        nodes.put(node.getName(), node);
    }

    public Set<String> getNodeNames() {
        return nodes.keySet();
    }
    public Node getNode(String name) {
        return nodes.get(name);
    }

    public void addMessage(NeuralMessage message) {
        messageLock.lock();
        messages.add(message.getType(), message);
        messageLock.unlock();
    }

    public void fire() {
        fireLock.lock();

        messageLock.lock();
        SetMap<String, NeuralMessage> currentMessages = messages;
        messages = new SetMap<>();
        messageLock.unlock();

        nodes.values().forEach(node -> {
            node.getMessageTypes().forEach(messageType -> {
                var messagesForType = currentMessages.getValues(messageType);
                if (messagesForType.size() > 0) {
                    node.handleMessage(messageType, messagesForType);
                }
            });
        });

        fireLock.unlock();
    }
}
