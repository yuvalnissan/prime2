package ai.prime.server;

public class AgentMessageBody {
    private String id;
    private String type;

    public AgentMessageBody(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
