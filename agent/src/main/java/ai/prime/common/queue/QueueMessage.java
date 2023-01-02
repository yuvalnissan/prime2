package ai.prime.common.queue;

import java.io.Serializable;

public interface QueueMessage extends Serializable {
    String getType();
}
