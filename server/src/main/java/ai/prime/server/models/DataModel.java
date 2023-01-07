package ai.prime.server.models;

import ai.prime.knowledge.data.base.ValueData;

import java.util.List;

public class DataModel {
    private String id;
    private String type;
    private List<DataModel> expressions;
    private String value;
    private final boolean negative;

    public DataModel(boolean negative, String id, String type, String value, List<DataModel> expressions) {
        this.negative = negative;
        this.id = id;
        this.type = type;
        this.value = value;
        this.expressions = expressions;
    }

    public String getType() {
        return type;
    }

    public boolean isNegative() {
        return negative;
    }

    public List<DataModel> getExpressions() {
        return expressions;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }
}
