package ai.prime.server;

import ai.prime.server.models.DataModel;

public class AgentMessageBody {
    private DataModel data;
    private String type;

    public AgentMessageBody(DataModel data, String type) {
        this.data = data;
        this.type = type;
    }

    public DataModel getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}
