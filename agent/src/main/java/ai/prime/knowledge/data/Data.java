package ai.prime.knowledge.data;

import java.util.Arrays;
import java.util.StringJoiner;

public class Data {
    private DataType type;
    private Expression[] expressions;
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

    public String getDisplayName() {
        if (displayName == null) {
            displayName = buildDisplayName();
        }

        return displayName;
    }

    public int hashCode(){
        if (hashCode == 0){
            hashCode = getDisplayName().hashCode();
        }

        return hashCode;
    }
}
