package ai.prime.agent;

import ai.prime.common.queue.QueueMessage;
import ai.prime.knowledge.data.Data;

public class FireMessage implements QueueMessage {
    public static final String TYPE = "fire";

    private Data data;

    public FireMessage(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
