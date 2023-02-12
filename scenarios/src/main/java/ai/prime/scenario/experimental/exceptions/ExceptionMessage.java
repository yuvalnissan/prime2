package ai.prime.scenario.experimental.exceptions;

import ai.prime.agent.NeuralMessage;
import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.nodes.confidence.PullValue;

public class ExceptionMessage extends NeuralMessage {
    public static final String TYPE = "exceptionMessage";

    private final PullValue exceptionPull;

    public ExceptionMessage(Data from, Data to, PullValue exceptionPull) {
        super(from, to);
        this.exceptionPull = exceptionPull;
    }

    public PullValue getExceptionPull() {
        return exceptionPull;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
