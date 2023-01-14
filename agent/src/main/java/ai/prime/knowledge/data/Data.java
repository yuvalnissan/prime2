package ai.prime.knowledge.data;

import java.util.Arrays;
import java.util.StringJoiner;

public class Data {
    private final DataType type;
    private final Expression[] expressions;
    private String displayName = null;
    private int hashCode = 0;

    public Data(DataType type, Expression[] expressions) {
        this.type = type;
        this.expressions = expressions;
    }

    public Data(DataType type) {
        this(type, new Expression[0]);
    }

    public DataType getType() {
        return type;
    }

    public Expression[] getExpressions() {
        return expressions;
    }

    private String buildDisplayName() {
        String value = type.getPredicate();

        StringJoiner stringJoiner = new StringJoiner(NamingUtil.LIST_SEPARATOR, NamingUtil.START_COMPLEX_EXPRESSION, NamingUtil.END_COMPLEX_EXPRESSION);
        Arrays.stream(expressions).forEach(expression -> stringJoiner.add(expression.getDisplayName()));
        value += stringJoiner.toString();

        return value;
    }

    public Data normalize() {
        //TODO deal with variable normalization
        return this;
    }

    public String getDisplayName() {
        if (displayName == null) {
            displayName = buildDisplayName();
        }

        return displayName;
    }

    @Override
    public int hashCode(){
        if (hashCode == 0){
            hashCode = getDisplayName().hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Data))
            return false;

        Data other = (Data)obj;

        return (getDisplayName().equals(other.getDisplayName()));
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
