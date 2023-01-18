package ai.prime.knowledge.data.base;

import ai.prime.knowledge.data.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariableData extends Data {
    public static final DataType TYPE = new DataType("value");

    public static final String PREFIX = "var" + NamingUtil.TYPE_SEPARATOR;
    public static final String SAME = "==";
    public static final String DIFFERENT = "!=";

    private final String name;
    private final Set<Expression> sameConstraints;
    private final Set<Expression> differentConstraints;

    public VariableData(String name, Set<Expression> same, Set<Expression> different) {
        super(TYPE);

        this.name = name.startsWith(PREFIX) ? name : PREFIX + name;
        this.sameConstraints = same;
        this.differentConstraints = different;
    }

    public VariableData(String name){
        this(name, new HashSet<>(), new HashSet<>());
    }

    @Override
    protected String buildDisplayName() {
        StringBuilder builder = new StringBuilder(name);
        for (Expression sameExpression : sameConstraints) {
            builder.append(SAME + NamingUtil.START_COMPLEX_EXPRESSION).append(sameExpression.getDisplayName()).append(NamingUtil.END_COMPLEX_EXPRESSION);
        }

        for (Expression differentExpression : differentConstraints) {
            builder.append(DIFFERENT + NamingUtil.START_COMPLEX_EXPRESSION).append(differentExpression.getDisplayName()).append(NamingUtil.END_COMPLEX_EXPRESSION);
        }

        return builder.toString();
    }

    public String getName(){
        return name;
    }

    public Set<Expression> getSameConstraints(){
        return sameConstraints;
    }

    public Set<Expression> getDifferentConstraints(){
        return differentConstraints;
    }

    @Override
    public VariableReference getVariableReference(){
        VariableReference variables = new VariableReference();
        variables.add(this);
        return variables;
    }

    public VariableData bindConstraints(Unification unification) {
        Set<Expression> boundSame = new HashSet<>();
        Set<Expression> boundDifferent = new HashSet<>();

        for (Expression same : sameConstraints) {
            boundSame.add(same.bind(unification, false));
        }

        for (Expression different : differentConstraints) {
            boundDifferent.add(different.bind(unification, false));
        }

        return new VariableData(name, boundSame, boundDifferent);
    }

    public boolean isValidAssignment(Expression assignment) {
        for (Expression same : sameConstraints) {
            if (same.getData().getType().equals(TYPE)) {
                return false;
            } else if (!same.equals(assignment)) {
                return false;
            }
        }

        for (Expression different : differentConstraints) {
            if (different.getData().getType().equals(TYPE)) {
                return false;
            } else if (different.equals(assignment)){
                return false;
            }
        }

        return true;
    }

    @Override
    public Expression replace(Map<Data, Expression> replace) {
        if (replace.containsKey(this))
            return replace.get(this);
        else
            return new Expression(this);
    }

    @Override
    public Unification unify(Expression exp, DataModifier modifier) {
        Unification answer = new Unification();
        answer.setMatch(name, exp.applyModifier(modifier));

        return answer;
    }
}
