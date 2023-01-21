package ai.prime.knowledge.data;

import ai.prime.knowledge.data.base.VariableData;

import java.util.*;

public class Data {
    private final DataType type;
    private final Expression[] expressions;
    private String displayName = null;
    private int hashCode = 0;

    private boolean isNormalized = false;
    private Data cachedNormalizedValue;
    private VariableReference variables;

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

    protected String buildDisplayName() {
        String value = type.getPredicate();

        StringJoiner stringJoiner = new StringJoiner(NamingUtil.LIST_SEPARATOR, NamingUtil.START_COMPLEX_EXPRESSION, NamingUtil.END_COMPLEX_EXPRESSION);
        Arrays.stream(expressions).forEach(expression -> stringJoiner.add(expression.getDisplayName()));
        value += stringJoiner.toString();

        return value;
    }

    public VariableReference getVariableReference() {
        if (variables == null){
            variables = new VariableReference();
            Arrays.stream(expressions).forEach(expression -> variables.add(expression.getData().getVariableReference()));
        }

        return variables;
    }

    private Set<Expression> getUpdatedVariableConstraints(Set<Expression> current, Map<String, String> updatedNames) {
        Set<Expression> updated = new HashSet<>();

        for (Expression constraint : current){
            if (constraint.getData().getType().equals(VariableData.TYPE)) {
                VariableData sameVar = (VariableData)constraint.getData();
                String updatedVarName = updatedNames.get(sameVar.getName());
                if (updatedVarName != null) {
                    VariableData referenceVariable = new VariableData(updatedVarName);
                    updated.add(new Expression(referenceVariable, constraint.getModifier()));
                }
            } else {
                updated.add(constraint);
            }
        }

        return updated;
    }

    public Expression replace(Map<Data, Expression> replace) {
        if (replace.containsKey(this)) {
            return replace.get(this);
        }

        Expression[] replacedExpressions = Arrays.stream(getExpressions()).map(expression -> expression.replace(replace)).toArray(Expression[]::new);
        Data replacedData = new Data(getType(), replacedExpressions);

        return new Expression(replacedData);
    }

    private Data getUpdatedVariableNames(Map<String, String> updatedNames) {
        Map<Data, Expression> replace = new HashMap<>();
        for (VariableData var : getVariableReference().getAllVariables()) {

            String updatedName = updatedNames.get(var.getName());
            Set<Expression> updatedSame = getUpdatedVariableConstraints(var.getSameConstraints(), updatedNames);
            Set<Expression> updatedDifferent = getUpdatedVariableConstraints(var.getDifferentConstraints(), updatedNames);

            VariableData updatedVariable = new VariableData(updatedName, updatedSame, updatedDifferent);
            replace.put(var, new Expression(updatedVariable));
        }

        return replace(replace).getData();
    }

    public Data addVariablePrefix(String prefix) {
        Map<String, String> updatedNames = new HashMap<>();

        for (VariableData var : getVariableReference().getAllVariables()) {
            if (!updatedNames.containsKey(var.getName())){
                updatedNames.put(var.getName(), VariableData.PREFIX + prefix + var.getName());
            }
        }

        return getUpdatedVariableNames(updatedNames);
    }

    public Unification unify(Data data) {
        return unify(new Expression(data), DataModifier.POSITIVE);
    }

    public Unification unify(Expression exp, DataModifier modifier) {
        if (!(exp.getData().getType().equals(this.getType()))) {
            return null;
        }

        Data other = exp.getData();

        if (getExpressions().length != other.getExpressions().length){
            return null;
        }

        Unification answer = new Unification();

        for (int i = 0 ; i < getExpressions().length ; i++) {
            Expression ownExpression = getExpressions()[i];
            Expression otherExpression = other.getExpressions()[i];

            Unification unification = ownExpression.unify(otherExpression);
            answer = answer.unify(unification);
            if (answer == null) {
                return null;
            }
        }

        return answer;
    }

    public Data bind(Unification unification, boolean shiftUnification) {
        if (shiftUnification) {
            unification = unification.shiftBoundPatterns();
        }

        Map<Data, Expression> replaceConstraints = new HashMap<>();
        for (VariableData variable : getVariableReference().getAllVariables()) {
            Expression boundVariableConstraints = new Expression(variable.bindConstraints(unification));
            replaceConstraints.put(variable, boundVariableConstraints);
        }

        Data boundConstraints = replace(replaceConstraints).getData();

        Map<Data, Expression> replace = new HashMap<>();
        for (VariableData variable : boundConstraints.getVariableReference().getAllVariables()) {

            Expression bound = unification.getBound(variable.getName());
            if (bound != null) {
                if (variable.isValidAssignment(bound)){
                    replace.put(variable, bound);
                } else {
                    return null;
                }
            }
        }

        return boundConstraints.replace(replace).getData();
    }

    private Data normalize(int startIndex){
        if (cachedNormalizedValue != null) {
            return cachedNormalizedValue;
        }

        Map<String, String> updatedNames = new HashMap<>();

        int counter = startIndex + 1;
        for (VariableData var : getVariableReference().getAllVariables()) {
            if (!updatedNames.containsKey(var.getName())) {
                updatedNames.put(var.getName(), VariableData.PREFIX + counter);
                counter++;
            }
        }

        Data normalized = getUpdatedVariableNames(updatedNames);
        normalized.isNormalized = true;
        cachedNormalizedValue = normalized;

        return normalized;
    }

    public boolean hasVariables() {
        return getVariableReference().getUniqueVariables().size() > 0;
    }

    public boolean isNormalized() {
        return isNormalized;
    }

    public Data normalize() {
        if (isNormalized()) {
            return this;
        }

        return normalize(0);
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
