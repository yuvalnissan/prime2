package ai.prime.knowledge.data;

import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.data.base.VariableData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Expression {
    private Data data;
    private DataModifier modifier;
    private String displayName = null;
    private int hashCode = 0;

    public Expression(Data data, DataModifier modifier) {
        this.data = data;
        this.modifier = modifier;
    }

    public Expression(Data data) {
        this(data, DataModifier.POSITIVE);
    }

    public Data getData() {
        return data;
    }

    public DataModifier getModifier() {
        return modifier;
    }

    public String getDisplayName() {
        if (displayName == null) {
            String dataDisplayName = data.getDisplayName();
            if (modifier == DataModifier.POSITIVE) {
                displayName = dataDisplayName;
            } else {
                displayName = modifier.getDisplayName() + NamingUtil.START_COMPLEX_EXPRESSION + dataDisplayName + NamingUtil.END_COMPLEX_EXPRESSION;
            }
        }

        return displayName;
    }

    public Expression applyModifier(DataModifier alter) {
        DataModifier appliedModifier = modifier.applyModifier(alter);

        return new Expression(data, appliedModifier);
    }

    public Expression not() {
        return applyModifier(DataModifier.NEGATIVE);
    }

    public Expression normalize(){
        if (!data.isNormalized()) {
            Data normalized = data.normalize();
            return new Expression(normalized, modifier);
        } else {
            return this;
        }
    }

    public  Unification unify(Expression exp){
        if ((!(getData().getType().equals(VariableData.TYPE)))){
            if (!modifier.equals(exp.getModifier())){
                return null;
            }

            if (exp.getData().getType().equals(VariableData.TYPE)){
                //Dealing with a case of unifying with a pattern
                return new Unification();
            }
        }

        Unification answer = data.unify(exp, modifier);
        return answer;
    }

    public Expression replace(Map<Data, Expression> replace) {
        if (replace.containsKey(data)){
            return replace.get(data).applyModifier(modifier);
        }

        return data.replace(replace).applyModifier(modifier);
    }

    public Expression bind(Unification unification, boolean shiftUnification) {
        Data bounded = data.bind(unification, shiftUnification);
        if (bounded != null) {
            return new Expression(bounded, modifier);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public int hashCode() {
        if (hashCode == 0){
            hashCode = getDisplayName().hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Expression))
            return false;

        return (toString().equals(obj.toString()));
    }

    //TODO ugly copy
    private static String[] splitVariableName(String value) {
        String[] result = new String[2];
        int sameIndex = value.indexOf(VariableData.SAME);
        int differentIndex = value.indexOf(VariableData.DIFFERENT);

        if (sameIndex >= 0 && (sameIndex < differentIndex || differentIndex < 0)) {
            result[0] = value.substring(0, sameIndex);
            result[1] = value.substring(sameIndex);
        } else if (differentIndex >= 0 && (differentIndex <= sameIndex || sameIndex < 0)) {
            result[0] = value.substring(0, differentIndex);
            result[1] = value.substring(differentIndex);
        } else {
            result[0] = value;
            result[1] = "";
        }

        return result;
    }

    //TODO ugly copy
    private static VariableData getVariableData(String value) {
        String[] split = splitVariableName(value);
        String name = split[0];
        String current = split[1];

        Set<Expression> same = new HashSet<>();
        Set<Expression> different = new HashSet<>();

        while (current.length() > 0) {
            if (current.startsWith(VariableData.SAME)) {
                current = current.substring(VariableData.SAME.length());
                String[] varSplit = splitVariableName(current);
                Expression expression = fromString(varSplit[0]);
                same.add(expression);
                current = varSplit[1];
            } else if (current.startsWith(VariableData.DIFFERENT)) {
                current = current.substring(VariableData.DIFFERENT.length());
                String[] varSplit = splitVariableName(current);
                Expression expression = fromString(varSplit[0]);
                different.add(expression);
                current = varSplit[1];

            } else {
                throw new RuntimeException("Not a legal state");
            }
        }

        return new VariableData(name, same, different);
    }

    private static Data extractPrimitive(String id) {
        if (id.startsWith(VariableData.PREFIX)) {
            return getVariableData(id);
        }

        return new ValueData(id);
    }

    public static Expression fromString(String id) {
        var cleanId = id.trim().replaceAll(" ", "");
        var start = cleanId.indexOf(NamingUtil.START_COMPLEX_EXPRESSION);
        if (start == -1) {
            return new Expression(extractPrimitive(cleanId));
        }

        if (!cleanId.substring(cleanId.length() - 1, cleanId.length()).equals(NamingUtil.END_COMPLEX_EXPRESSION)) {
            throw new RuntimeException("Invalid ID format");
        }

        var type = cleanId.substring(0, start);
        var internal = cleanId.substring(start + 1, cleanId.length() - 1);
        if (type.equalsIgnoreCase(DataModifier.NEGATIVE.getDisplayName())) {
            return fromString(internal).not();
        }

        var expressionIds = NamingUtil.separate(internal, NamingUtil.LIST_SEPARATOR);
        Expression[] expressions = Arrays.stream(expressionIds).map(Expression::fromString).toArray(Expression[]::new);
        Data data = new Data(new DataType(type), expressions);

        return new Expression(data);
    }
}
