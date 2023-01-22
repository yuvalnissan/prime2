package ai.prime.common.queue;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class MessageQueue<T extends QueueMessage> {
    private BlockingQueueWithConfirmation<T> queue;
    private final Set<MessageConsumer> messageConsumers;
    private final Set<Thread> threads;

    public MessageQueue() {
        queue = new BlockingQueueWithConfirmation<T>();
        messageConsumers = new HashSet<>();
        threads = new HashSet<>();
    }

    public void add(T message) {
        queue.add(message);
    }

    public long size() {
        return queue.size();
    }

    public void registerConsumer(Consumer<T> consumer) {
        MessageConsumer<T> messageConsumer = new MessageConsumer<T>(queue, consumer);
        messageConsumers.add(messageConsumer);
        Thread thread = new Thread(messageConsumer);
        threads.add(thread);
        thread.start();
    }

    public boolean isProcessing() {
        return queue.isProcessing();
    }

    public void stop() {
        queue.clear();
        threads.forEach(Thread::interrupt);
    }

    public void pause() {
        messageConsumers.forEach(consumer -> consumer.setPaused(true));
    }

    public void resume() {
        messageConsumers.forEach(consumer -> consumer.setPaused(true));
    }
}
