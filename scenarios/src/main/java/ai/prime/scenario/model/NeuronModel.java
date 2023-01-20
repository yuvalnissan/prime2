package ai.prime.scenario.model;

public class NeuronModel {
    private final String confidence;
    private final DataModel data;

    public NeuronModel(String confidence, DataModel data) {
        this.confidence = confidence;
        this.data = data;
    }

    public String getConfidence() {
        return confidence;
    }

    public DataModel getData() {
        return data;
    }
}
