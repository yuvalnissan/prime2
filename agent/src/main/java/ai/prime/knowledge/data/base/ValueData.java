package ai.prime.knowledge.data.base;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.DataType;

public class ValueData extends Data {
    public static final DataType TYPE = new DataType("value");
    private String value;

    public ValueData(String value) {
        super(TYPE);
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
