package ai.prime.knowledge.data;

import ai.prime.knowledge.data.base.VariableData;

import java.util.Map;

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
}
