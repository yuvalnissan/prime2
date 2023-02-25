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

        if (pull < 0.0){
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
        return "(" + format.format(pull) + ", " + isPositive + ", " + format.format(potentialResistance) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PullValue pullValue = (PullValue) o;

        if (isPositive != pullValue.isPositive) return false;
        if (Double.compare(pullValue.pull, pull) != 0) return false;
        return Double.compare(pullValue.potentialResistance, potentialResistance) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (isPositive ? 1 : 0);
        temp = Double.doubleToLongBits(pull);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(potentialResistance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
