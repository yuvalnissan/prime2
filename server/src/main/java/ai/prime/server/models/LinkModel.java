package ai.prime.server.models;


public class LinkModel {
    private final String type;
    private final DataModel from;
    private final DataModel to;
    private final double strength;

    public LinkModel(String type, DataModel from, DataModel to, double strength) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.strength = strength;
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

    public double getStrength() {
        return strength;
    }
}
