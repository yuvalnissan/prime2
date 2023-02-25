package ai.prime.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics {

    private final AtomicLong fireCounter;
    private final Map<String, AtomicLong> messageTypeCounter;

    public Statistics() {
        this.fireCounter = new AtomicLong(0);
        this.messageTypeCounter = new HashMap<>();
    }

    public void addFire() {
        fireCounter.incrementAndGet();
    }

    public void addMessage(NeuralMessage message) {
        if (!messageTypeCounter.containsKey(message.getType())) {
            // Not atomic, but worse case a few messages will not be counted
            messageTypeCounter.put(message.getType(), new AtomicLong(0));
        }

        messageTypeCounter.get(message.getType()).incrementAndGet();
    }

    public long getFireCount() {
        return fireCounter.get();
    }

    public Map<String, AtomicLong> getMessageTypeCounter() {
        return messageTypeCounter;
    }
}
