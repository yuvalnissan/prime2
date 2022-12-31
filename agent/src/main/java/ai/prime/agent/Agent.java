package ai.prime.agent;

import ai.prime.knowledge.memory.Memory;

public class Agent {

    private String name;
    private Memory memory;

    public Agent(String name) {
        this.name = name;
        this.memory = new Memory(this);
    }

    public String getName() {
        return name;
    }

    public Memory getMemory() {
        return memory;
    }
}
