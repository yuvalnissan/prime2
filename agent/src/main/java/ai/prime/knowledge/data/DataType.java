package ai.prime.knowledge.data;

import ai.prime.common.utils.Type;

public class DataType extends Type {

    public DataType(String predicate) {
        super(predicate);
    }

    public String getPredicate() {
        return super.getName();
    }
}
