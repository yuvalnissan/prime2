package ai.prime.knowledge.data.base;

import ai.prime.knowledge.data.*;

import java.util.Map;

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
    protected String buildDisplayName() {
        return getValue();
    }

    @Override
    public Expression replace(Map<Data, Expression> replace) {
        if (replace.containsKey(this)) {
            return replace.get(this);
        }

        return new Expression(this);
    }

    @Override
    public Unification unify(Expression exp, DataModifier modifier) {
        if (exp.getData().getType().equals(TYPE) && getDisplayName().equals(exp.getData().getDisplayName())) {
            return new Unification();
        }

        return null;
    }
}
