package ai.prime.knowledge.nodes.connotation;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;

public class IgniteMessage extends NeuralMessage {
    public static final String TYPE = "igniteMessage";

    private final double strength;

    public IgniteMessage(Data from, Data to, double strength) {
        super(from, to);

        this.strength = strength;
    }

    public double getStrength() {
        return strength;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
