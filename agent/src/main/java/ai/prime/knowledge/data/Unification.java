package ai.prime.knowledge.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Unification {
    private Map<String, Expression> binding;

    public Unification() {
        binding = new HashMap<String, Expression>();
    }

    public boolean setMatch(String varName, Expression solving) {
        Expression bound = binding.get(varName);

        if (bound != null && !bound.equals(solving)){
            //Can't bind the same variable to different expression
            return false;
        }

        binding.put(varName, solving);

        return true;
    }

    public Expression getBound(String varName) {
        return binding.get(varName);
    }

    /**
     * merges with another answer. Returns null if there is not merge.
     */
    public Unification unify(Unification other) {
        if (other == null) {
            return null;
        }

        Unification newAnswer = new Unification();
        for (String var : binding.keySet()) {
            Expression val = binding.get(var);
            newAnswer.setMatch(var, val);
        }

        boolean success = true;
        for (String var : other.binding.keySet()) {
            Expression val = other.getBound(var);
            success = newAnswer.setMatch(var, val);
            //return false if there is a conflict
            if (!success) {
                return null; //Can't merge these two
            }
        }

        return newAnswer;
    }

    public Set<String> getBoundedVariables(){
        return binding.keySet();
    }

    public Unification shiftBoundPatterns() {
        Unification unification = new Unification();
        for (String varName : binding.keySet()) {
            Expression boundExpression = binding.get(varName);
            //Expression shiftedExpression = new Expression(boundExpression.getData().shift(shiftIndex), boundExpression.getModifier());
            Expression shiftedExpression = new Expression(boundExpression.getData().addVariablePrefix("test"), boundExpression.getModifier());
            unification.setMatch(varName, shiftedExpression);
        }

        return unification;
    }
}
