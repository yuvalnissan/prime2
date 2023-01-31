package ai.prime.agent.interaction;

import ai.prime.knowledge.data.Data;

public interface Actuator {

    public String getMappedAction();

    public void act(Data data);

    public void cancel(Data data);
}
