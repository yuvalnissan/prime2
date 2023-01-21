package ai.prime.knowledge.nodes.binding;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class BindingMessage extends NeuralMessage {
    public static final String TYPE = "bindingMessage";

    private final BindingMatch bindingMatch;

    public BindingMessage(Data from, Data to, BindingMatch bindingMatch) {
        super(from, to);
        this.bindingMatch = bindingMatch;
    }

    public BindingMatch getBindingMatch() {
        return bindingMatch;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
