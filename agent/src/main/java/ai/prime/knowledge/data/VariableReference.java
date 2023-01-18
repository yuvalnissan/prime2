package ai.prime.knowledge.data;

import ai.prime.knowledge.data.base.VariableData;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class VariableReference {
    private List<VariableData> leftToRight; //ordered and repeating if not unique
    private Set<VariableData> uniqueVariables;

    public VariableReference() {
        leftToRight = new LinkedList<>();
        uniqueVariables = new HashSet<>();
    }

    public List<VariableData> getAllVariables() {
        return leftToRight;
    }

    public Set<VariableData> getUniqueVariables() {
        return uniqueVariables;
    }

    public void add(VariableData var) {
        leftToRight.add(var);
        uniqueVariables.add(var);
    }

    public void add(VariableReference reference) {
        for (VariableData var : reference.leftToRight){
            add(var);
        }
    }

    public boolean isOverlapping(VariableReference other) {
        Set<VariableData> set1 = getUniqueVariables();
        Set<VariableData> set2 = other.getUniqueVariables();

        final var larger = set1.size() > set2.size() ? set1 : set2;
        final var smaller = set1.size() > set2.size() ? set2 : set1;

        return smaller.stream().anyMatch(larger::contains);
    }

    public String toString(){
        return leftToRight.toString();
    }
}
