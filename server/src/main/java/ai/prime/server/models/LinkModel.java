package ai.prime.server.models;


public class LinkModel {
    private final String type;
    private final DataModel from;
    private final DataModel to;

    public LinkModel(String type, DataModel from, DataModel to) {
        this.type = type;
        this.from = from;
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public DataModel getFrom() {
        return from;
    }

    public DataModel getTo() {
        return to;
    }
}
