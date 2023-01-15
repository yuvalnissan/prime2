package ai.prime.agent;

import ai.prime.common.queue.MessageQueue;
import ai.prime.common.queue.QueueMessage;

import java.util.HashMap;
import java.util.Map;

public class QueueManager {
    private final Map<String, MessageQueue<QueueMessage>> queues;

    public QueueManager() {
        this.queues = new HashMap<>();
    }

    public MessageQueue<QueueMessage> addQueue(String queueName) {
        if (queues.containsKey(queueName)) {
            throw new RuntimeException("Queue " + queueName + " already exists");
        }

        MessageQueue<QueueMessage> queue = new MessageQueue<>();
        queues.put(queueName, queue);

        return queue;
    }

    public MessageQueue<QueueMessage> getQueue(String queueName) {
        if (!queues.containsKey(queueName)) {
            throw new RuntimeException("Queue " + queueName + " does not exist");
        }

        return queues.get(queueName);
    }

    public boolean hasPendingMessages() {
        return queues.values().stream().anyMatch(queue -> queue.size() > 0 || queue.isProcessing());
    }
}
