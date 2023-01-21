package ai.prime.knowledge.nodes.binding;

import ai.prime.agent.NeuralEvent;
import ai.prime.knowledge.data.Data;

public class ResolvedPatternEvent extends NeuralEvent {
    public static final String TYPE = "resolvedPatternEvent";

    private final Data resolved;

    public ResolvedPatternEvent(Data resolved) {
        this.resolved = resolved;
    }

    public Data getResolved() {
        return resolved;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
