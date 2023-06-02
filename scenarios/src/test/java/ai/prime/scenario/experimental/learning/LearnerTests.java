package ai.prime.scenario.experimental.learning;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.SenseConfidence;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class LearnerTests {

    @Test
    public void testSimplerCase() {
        var pattern = Expression.fromString("canDo(act(move, var:from, var:to))").getData();
        var learner = new Learner(pattern, System.out::println);

        var evidence1Data = Expression.fromString("canDo(act(move, T1, T3))").getData();
        Map<Data, Confidence> evidence1conditions = new HashMap<>();
        evidence1conditions.put(Expression.fromString("rel(on, D1, T1)").getData(), SenseConfidence.SENSE_POSITIVE);
        evidence1conditions.put(Expression.fromString("won").getData(), SenseConfidence.SENSE_NEGATIVE);
        var evidence1 = new Evidence(evidence1Data, SenseConfidence.SENSE_POSITIVE, evidence1conditions);

        var evidence2Data = Expression.fromString("canDo(act(move, T3, T1))").getData();
        Map<Data, Confidence> evidence2conditions = new HashMap<>();
        evidence2conditions.put(Expression.fromString("rel(on, D1, T1)").getData(), SenseConfidence.SENSE_POSITIVE);
        evidence2conditions.put(Expression.fromString("won").getData(), SenseConfidence.SENSE_NEGATIVE);
        var evidence2 = new Evidence(evidence2Data, SenseConfidence.SENSE_NEGATIVE, evidence2conditions);

        var evidence3Data = Expression.fromString("canDo(act(move, T1, T2))").getData();
        Map<Data, Confidence> evidence3conditions = new HashMap<>();
        evidence3conditions.put(Expression.fromString("rel(on, D1, T1)").getData(), SenseConfidence.SENSE_POSITIVE);
        evidence3conditions.put(Expression.fromString("won").getData(), SenseConfidence.SENSE_NEGATIVE);
        var evidence3 = new Evidence(evidence3Data, SenseConfidence.SENSE_POSITIVE, evidence3conditions);

        learner.addEvidence(evidence1);
        learner.addEvidence(evidence2);
        learner.addEvidence(evidence3);
//
//
//        assertNotNull(unify);
//        assertEquals(unify.getBoundedVariables().size(), 1);
//        assertEquals(unify.getBound("var:1"), getValue("C"));
    }
}
