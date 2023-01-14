package ai.prime.knowledge.nodes.confidence;

import java.text.DecimalFormat;

public class InferredConfidence extends Confidence {
    private static final DecimalFormat format = new DecimalFormat("###.##");

    public static final Confidence EMPTY = new InferredConfidence(0.0, 0.0, 0.0);

    public InferredConfidence(double strength, double positiveResistance, double negativeResistance) {
        super(strength, positiveResistance, negativeResistance);
    }

    @Override
    public Confidence invert() {
        return new InferredConfidence((-1.0) * getStrength(), getResistance(false), getResistance(true));
    }

    @Override
    protected String getDisplayedName() {
        double positive = getResistance(true);
        double negative = getResistance(false);
        String strengthStr = format.format(getStrength());
        String positiveStr = format.format(positive);
        String negativeStr = format.format(positive);

        return strengthStr + "|" + positiveStr + "|" + negativeStr;
    }
}
