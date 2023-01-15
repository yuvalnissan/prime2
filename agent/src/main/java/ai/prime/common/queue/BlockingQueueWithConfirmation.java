package ai.prime.common.queue;

import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueWithConfirmation<T extends QueueMessage> {

    private final ArrayBlockingQueue<T> queue;
    private int processingCount;

    public BlockingQueueWithConfirmation() {
        queue = new ArrayBlockingQueue<>(10000000);
        processingCount = 0;
    }

    public boolean isEmpty() {
        return queue.isEmpty() && processingCount == 0;
    }

    public synchronized void setProcessing(boolean started) {
        if (started) {
            processingCount++;
        } else {
            processingCount--;
        }
    }

    public boolean isProcessing() {
        return processingCount > 0;
    }

    public void add(T message) {
        queue.add(message);
    }

    public T take() throws InterruptedException {
        return queue.take();
    }

    public long size() {
        return queue.size();
    }
}
