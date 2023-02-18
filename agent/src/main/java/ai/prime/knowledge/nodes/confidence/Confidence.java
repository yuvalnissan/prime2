package ai.prime.knowledge.nodes.confidence;

import ai.prime.common.utils.Settings;

public abstract class Confidence {
    private static final String CONFIGURATION_SIGNIFICANCE_CONFIDENCE = "confidence.sensitivity.minimum.percent.strength";
    private static final String CONFIGURATION_SIGNIFICANCE_RESISTANCE = "confidence.sensitivity.minimum.percent.resistance";
    public static final double RESISTANCE_MINIMUM = Settings.getDoubleProperty("confidence.resistance.minimum.threshold");

    private static final double CONFIDENCE_SIGNIFICANT_DIFF = Settings.getDoubleProperty(CONFIGURATION_SIGNIFICANCE_CONFIDENCE);
    private static final double CONFIDENCE_RESISTANCE_DIFF = Settings.getDoubleProperty(CONFIGURATION_SIGNIFICANCE_RESISTANCE);

    private final double strength;
    private final double positiveResistance;
    private final double negativeResistance;
    private String cachedDisplayName;

    public Confidence(double strength, double positiveResistance, double negativeResistance) {
        this.strength =  Math.max(Math.min(strength, 1.0), -1.0);
        this.positiveResistance = positiveResistance < RESISTANCE_MINIMUM ? 0.0 : positiveResistance;
        this.negativeResistance = negativeResistance < RESISTANCE_MINIMUM ? 0.0 : negativeResistance;
    }

    public double getStrength() {
        return strength;
    }

    public double getPositiveResistance() {
        return positiveResistance;
    }

    public double getNegativeResistance() {
        return negativeResistance;
    }

    public double getResistance(boolean isPositive) {
        return isPositive ? positiveResistance : negativeResistance;
    }

    @Override
    public String toString(){
        if (cachedDisplayName == null){
            cachedDisplayName = getDisplayedName();
        }

        return cachedDisplayName;
    }

    public abstract Confidence invert();

    protected abstract String getDisplayedName();

    public boolean isSignificantlyDifferent(Confidence other) {
        if (this.getStrength() == 0.0 && other.getStrength() != 0) {
            return true;
        }

        //TODO finalize the decision
//        if (Math.abs((other.getStrength() / this.getStrength()) - 1.0) > CONFIDENCE_SIGNIFICANT_DIFF) {
        if (Math.abs(other.getStrength() - this.getStrength()) > CONFIDENCE_SIGNIFICANT_DIFF) {
            return true;
        }

        if (this.getResistance(true) == 0.0 && other.getResistance(true) != 0.0) {
            return true;
        }

        if (Math.abs((other.getResistance(true) / this.getResistance(true)) - 1.0) > CONFIDENCE_RESISTANCE_DIFF){
            return true;
        }

        if (this.getResistance(false) == 0.0 && other.getResistance(false) != 0.0) {
            return true;
        }

        if (Math.abs((other.getResistance(false) / this.getResistance(false)) - 1.0) > CONFIDENCE_RESISTANCE_DIFF){
            return true;
        }

        return false;
    }

    public int hashCode(){
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        return obj.toString().equals(toString());
    }
}
