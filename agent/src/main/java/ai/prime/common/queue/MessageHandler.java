package ai.prime.common.queue;

public interface MessageHandler<T extends QueueMessage> {
    void handle(T message);
}
