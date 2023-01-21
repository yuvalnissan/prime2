package ai.prime.knowledge.nodes.binding;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class ReceptorConnectMessage extends NeuralMessage {
    public static final String TYPE = "receptorConnectMessage";

    public ReceptorConnectMessage(Data from, Data to) {
        super(from, to);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
