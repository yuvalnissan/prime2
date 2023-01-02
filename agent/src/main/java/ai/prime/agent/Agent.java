package ai.prime.agent;

import ai.prime.common.queue.QueueMessage;
import ai.prime.common.queue.MessageQueue;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.memory.Memory;

import java.util.Date;
import java.util.stream.IntStream;

public class Agent {
    private static final String FIRE_QUEUE = "fire";
    private static final int FIRE_QUEUE_SIZE = 1;

    private final String name;
    private final Memory memory;
    private final QueueManager queueManager;


    public Agent(String name) {
        this.name = name;
        this.memory = new Memory(this);
        this.queueManager = new QueueManager();

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

    public void sendMessageToNeuron(NeuralMessage message) {
        Data to = message.getTo();
        getMemory().getNeuron(to).addMessage(message);
        queueManager.getQueue(FIRE_QUEUE).add(new FireMessage(to));
    }

    public boolean waitForStability(int timeout){
        long startTime = (new Date()).getTime();
        long passed = 0;

        try {
            while (passed < timeout) {
                if (!queueManager.hasPendingMessages()) {
                    return true;
                }

                Thread.sleep(1000);
                passed = (new Date()).getTime() - startTime;
            }

            System.out.println("Failed to stabilize");
            return false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
