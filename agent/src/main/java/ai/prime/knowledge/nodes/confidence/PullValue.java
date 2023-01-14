package ai.prime.knowledge.nodes.confidence;

import ai.prime.knowledge.data.Data;

import java.text.DecimalFormat;

public class PullValue {
    public static final PullValue EMPTY = new PullValue(true, 0.0, 0.0, null);
    private static final DecimalFormat format = new DecimalFormat("###.##");

    private final boolean isPositive;
    private final double pull;
    private final double potentialResistance;
    private final Data source;

    public PullValue(boolean isPositive, double pull, double potentialResistance, Data source){
        this.isPositive = isPositive;
        this.pull = pull;
        this.potentialResistance = potentialResistance;
        this.source = source;

        if (pull<0.0){
            throw new RuntimeException("Can't set negative resistance: " + pull);
        }
    }

    public boolean isPositive() {
        return isPositive;
    }

    public double getPull() {
        return pull;
    }

    public double getPotentialResistance() {
        return potentialResistance;
    }

    public Data getSource() {
        return source;
    }

    @Override
    public String toString(){
        return "(" + format.format(pull) + ", " + isPositive + ", " + format.format(potentialResistance) + ", " + (source !=null ? ", " + source : "") + ")";
    }

    @Override
    public boolean equals(Object other){
        return toString().equals(other.toString());
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }
}
