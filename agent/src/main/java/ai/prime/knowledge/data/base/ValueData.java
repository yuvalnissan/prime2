package ai.prime.knowledge.data.base;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataType;

public class ValueData extends Data {

    private String value;

    public ValueData(String value) {
        super(new DataType("value"));
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getDisplayName() {
        return getValue();
    }
}
