package ai.prime.knowledge.neuron;

import ai.prime.agent.Agent;
import ai.prime.agent.NeuralEvent;
import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.ListMap;
import ai.prime.common.utils.Lock;
import ai.prime.common.utils.Logger;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.nodes.Node;

import java.util.*;

public class Neuron {
    private final Agent agent;
    private final Data data;
    private final Links links;
    private final Map<String, Node> nodes;

    private ListMap<String, NeuralMessage> messages;
    private Set<NeuralEvent> events;

    private final Lock fireLock;
    private final Lock messageLock;
    private final Lock eventLock;

    public Neuron(Agent agent, Data data){
        this.agent = agent;
        this.data = data;
        this.links = new Links();
        this.nodes = new HashMap<>();
        this.messages = new ListMap<>();
        this.events = new HashSet<>();

        this.messageLock = new Lock();
        this.fireLock = new Lock();
        this.eventLock = new Lock();
    }

    public Agent getAgent() {
        return agent;
    }

    public Data getData() {
        return data;
    }

    public Collection<Link> getLinks(LinkType type) {
        return links.getLinks(type);
    }

    public boolean hasLink(LinkType type, Data data) {
        return links.hasLink(type, data);
    }

    public Collection<LinkType> getLinkTypes() {
        return links.getTypes();
    }

    public void addLink(Link link) {
        links.addLink(link);
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
        //TODO fix a case where two messages arrive with conflicting values. Only one will be processed, but it may not be the latest one
        Logger.debug("neuron", () -> "add message to " + getData().getDisplayName() + ": " + message.toString());
        messageLock.lock();
        messages.add(message.getType(), message);
        messageLock.unlock();
    }

    public void addEvent(NeuralEvent event) {
        Logger.debug("neuron", () -> "add event: " + event.toString());
        eventLock.lock();
        events.add(event);
        eventLock.unlock();
    }

    private void handleEvents() {
        int counter = 0;
        while (!events.isEmpty()){
            if (counter > 20) {
                Logger.error("Event endless loop!!!");
                throw new RuntimeException("Endless event loop");
            }
            eventLock.lock();
            Set<NeuralEvent> current = events;
            events = new HashSet<>();
            eventLock.unlock();

            current.forEach(event -> nodes.values().forEach(node -> node.handleEvent(event)));

            counter++;
        }
    }

    public void fire() {
        fireLock.lock();

        messageLock.lock();
        ListMap<String, NeuralMessage> currentMessages = messages;
        messages = new ListMap<>();
        messageLock.unlock();

        nodes.values().forEach(node -> {
            node.getMessageTypes().forEach(messageType -> {
                var messagesForType = currentMessages.getValues(messageType);
                if (messagesForType.size() > 0) {
                    node.handleMessage(messageType, messagesForType);
                }
            });
        });

        handleEvents();

        fireLock.unlock();
    }
}
