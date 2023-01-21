package ai.prime.server.models;

import java.util.List;

public class DataModel {
    private String id;
    private String type;
    private List<DataModel> expressions;
    private String value;
    private String var;
    private final boolean negative;

    public DataModel(boolean negative, String id, String type, String value, String var, List<DataModel> expressions) {
        this.negative = negative;
        this.id = id;
        this.type = type;
        this.value = value;
        this.var = var;
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

    public String getId() {
        return id;
    }
}
