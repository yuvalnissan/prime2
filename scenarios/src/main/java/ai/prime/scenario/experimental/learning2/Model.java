package ai.prime.scenario.experimental.learning2;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.InferredConfidence;
import ai.prime.scenario.experimental.learning.Learner;

import java.util.*;


public class Model {
    private static final int ATTENTION_CAP = 10;
    private static final int CONDITION_CAP = 3;

    private TreeSet<Data> active;
    private Map<Data, Confidence> confidences;
    private Map<Data, Double> attention;
    private Map<Data, Learner> learners;

    private Set<Data> rules;

    private void flush() {
        active = new TreeSet<>((Data data1, Data data2)-> {
            Double connotation1 = attention.getOrDefault(data1, 0.0);
            Double connotation2 = attention.getOrDefault(data2, 0.0);
            return connotation1.compareTo(connotation2);
        });
    }

    public Model() {
        confidences = new HashMap<>();
        attention = new HashMap<>();
        learners = new HashMap<>();

        rules = new HashSet<>();

        flush();
    }

    public void add(Data data, double attention, Confidence confidence) {
        Confidence currentConfidence = confidences.getOrDefault(data, InferredConfidence.EMPTY);

        // calculate surprise

        // build evidence(s)

        // bookkeeping on the active

    }

    private void addRule(Data rule) {
        rules.add(rule);
    }


    public void learn(Data pattern) {
        Learner learner = learners.getOrDefault(pattern, new Learner(pattern, this::addRule));

        // ...
    }

    public void sleep() {
        flush();
    }

    public Collection<Data> getRules() {
        return rules;
    }
}
