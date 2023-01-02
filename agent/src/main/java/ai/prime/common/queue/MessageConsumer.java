package ai.prime.common.queue;

import java.util.function.Consumer;

public class MessageConsumer<T extends QueueMessage> implements Runnable {
    private final BlockingQueueWithConfirmation<T> queue;
    private Consumer<T> consumer;
    private boolean isPaused = false;

    public MessageConsumer(BlockingQueueWithConfirmation<T> queue, Consumer<T> consumer) {
        this.queue = queue;
        this.consumer = consumer;
    }

    public void setPaused(boolean isPaused){
        this.isPaused = isPaused;
    }

    private void handleMessage(T message) {
        consumer.accept(message);
    }

    @Override
    public void run() {
        try{
            while(true){
               if (isPaused){
                    Thread.sleep(1000);
                }else{
                    T message = queue.take();
                    queue.setProcessing(true);
                    handleMessage(message);
                    queue.setProcessing(false);
                }
            }
        } catch(InterruptedException e) {
            System.out.println("Interrupted");
        } catch(Exception e){
            System.err.println("Failure getting message from queue");
        }
    }
}
