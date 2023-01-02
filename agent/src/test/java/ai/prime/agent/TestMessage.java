package ai.prime.agent;

import ai.prime.common.queue.QueueMessage;
import ai.prime.knowledge.data.Data;

public class TestMessage extends NeuralMessage {
    private String message;

    public TestMessage(Data from, Data to, String message) {
        super(from, to);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getType() {
        return "test";
    }

    @Override
    public String toString() {
        return message;
    }
}
