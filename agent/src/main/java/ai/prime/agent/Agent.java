package ai.prime.agent;

import ai.prime.agent.interaction.Actuator;
import ai.prime.common.queue.MessageQueue;
import ai.prime.common.queue.QueueMessage;
import ai.prime.common.utils.Logger;
import ai.prime.common.utils.Settings;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.memory.Memory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class Agent {
    private static final String FIRE_QUEUE = "fire";
    private static final int FIRE_QUEUE_SIZE = Settings.getIntProperty("neuron.fire.queue.size");

    private final String name;
    private final Memory memory;
    private final QueueManager queueManager;

    private final NodeMapping nodeMapping;
    private final Map<String, Actuator> actuators;

    private final AtomicLong fireCounter;


    public Agent(String name) {
        this.name = name;
        this.memory = new Memory(this);
        this.queueManager = new QueueManager();
        this.nodeMapping = new NodeMapping();
        this.fireCounter = new AtomicLong(0);
        this.actuators = new HashMap<>();

        MessageQueue<QueueMessage> neuronQueue = this.queueManager.addQueue(FIRE_QUEUE);
        IntStream.range(0, FIRE_QUEUE_SIZE).forEach(i -> neuronQueue.registerConsumer(message -> {
            FireMessage fireMessage = (FireMessage) message;
            getMemory().getNeuron(fireMessage.getData()).fire();
        }));
    }

    public String getName() {
        return name;
    }

    public Memory getMemory() {
        return memory;
    }

    public NodeMapping getNodeMapping() {
        return nodeMapping;
    }

    public long getMessageCount() {
        return fireCounter.get();
    }

    public void registerActuator(Actuator actuator) {
        if (actuators.containsKey(actuator.getMappedAction())) {
            throw new RuntimeException("Actuator already registered for " + actuator.getMappedAction());
        }

        actuators.put(actuator.getMappedAction(), actuator);
    }

    public Actuator getActuator(String action) {
        return actuators.get(action);
    }

    public void sendMessageToNeuron(NeuralMessage message) {
        Logger.debug("agent", "sending message to " + message.getTo().getDisplayName());
        Data to = message.getTo().normalize();
        getMemory().getNeuron(to).addMessage(message);
        queueManager.getQueue(FIRE_QUEUE).add(new FireMessage(to));
        fireCounter.incrementAndGet();
    }

    public boolean isStable() {
        return !queueManager.hasPendingMessages();
    }

    public boolean waitForStability(int timeout) {
        long startTime = (new Date()).getTime();
        long passed = 0;

        try {
            while (passed < timeout) {
                if (isStable()) {
                    return true;
                }

                Thread.sleep(1000);
                passed = (new Date()).getTime() - startTime;
            }

            Logger.error("Failed to stabilize");
            return false;
        } catch (InterruptedException e) {
            Logger.error("Agent interrupted", e);
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        queueManager.stop();
    }

    public void pause() {
        queueManager.pause();
    }

    public void resume() {
        queueManager.resume();
    }
}
