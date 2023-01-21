package ai.prime.scenario.model;

import java.util.List;

public class DataModel {
    private String type;
    private List<DataModel> expressions;
    private String value;
    private String var;
    private final boolean negative;

    public DataModel(boolean negative, String value) {
        this.negative = negative;
        this.value = value;
    }

    public DataModel(boolean negative, String type, List<DataModel> expressions) {
        this.negative = negative;
        this.type = type;
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

    public String getVar() {
        return var;
    }
}
