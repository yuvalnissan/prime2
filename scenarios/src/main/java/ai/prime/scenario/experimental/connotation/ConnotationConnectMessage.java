package ai.prime.scenario.experimental.connotation;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class ConnotationConnectMessage extends NeuralMessage {
    public static final String TYPE = "connotationConnectMessage"; //TODO optimize by having the type register the relevant node

    public ConnotationConnectMessage(Data from, Data to) {
        super(from, to);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
