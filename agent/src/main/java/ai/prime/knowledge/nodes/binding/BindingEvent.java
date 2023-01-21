package ai.prime.knowledge.nodes.binding;

import ai.prime.agent.NeuralEvent;

public class BindingEvent extends NeuralEvent {
    public static final String TYPE = "bindingEvent";

    private final BindingMatch match;

    public BindingEvent(BindingMatch match) {
        this.match = match;
    }

    public BindingMatch getMatch() {
        return match;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
