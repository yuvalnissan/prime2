package ai.prime.knowledge.nodes.confidence;

import java.text.DecimalFormat;

public class SenseConfidence extends Confidence {
    public static final double SENSE_PULL = 100003.0;
    private static final DecimalFormat format = new DecimalFormat("###.##");

    public static final SenseConfidence SENSE_EMPTY = new SenseConfidence(0.0, true);
    public static final SenseConfidence SENSE_POSITIVE = new SenseConfidence(1.0, true);
    public static final SenseConfidence SENSE_NEGATIVE = new SenseConfidence(-1.0, false);

    private final boolean isPositive;

    public SenseConfidence(double strength, boolean isPositive) {
        super(strength, isPositive ? SENSE_PULL : 0.0, isPositive ? 0.0 : SENSE_PULL);
        this.isPositive = isPositive;
    }

    public boolean isPositive() {
        return isPositive;
    }

    @Override
    public Confidence invert() {
        return new SenseConfidence((-1.0) * getStrength(), !isPositive);
    }

    @Override
    protected String getDisplayedName() {
        String strengthStr = format.format(getStrength());

        return strengthStr + "|SENSE_" + (isPositive ? "POSITIVE" : "NEGATIVE");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SenseConfidence)) {
            return false;
        }

        return super.equals(obj);
    }
}
