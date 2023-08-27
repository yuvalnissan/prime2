package ai.prime.scenario.experimental.learning2;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.Expression;
import ai.prime.knowledge.nodes.confidence.Confidence;
import ai.prime.knowledge.nodes.confidence.SenseConfidence;
import ai.prime.scenario.experimental.learning.Evidence;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ModelTests {

    @Test
    public void testSimplerCase() {
        var model = new Model();


        var evidence1Data = Expression.fromString("canDo(act(move, T1, T3))").getData();

        model.add(Expression.fromString("won").getData(), 1.0, SenseConfidence.SENSE_POSITIVE);
        model.add(Expression.fromString("rel(on, D1, T1)").getData(), 1.0, SenseConfidence.SENSE_POSITIVE);
        model.add(Expression.fromString("canDo(act(move, T1, T3))").getData(), 1.0, SenseConfidence.SENSE_POSITIVE);
        model.add(Expression.fromString("canDo(act(move, T3, T1))").getData(), 1.0, SenseConfidence.SENSE_NEGATIVE);
        model.add(Expression.fromString("canDo(act(move, T1, T2))").getData(), 1.0, SenseConfidence.SENSE_POSITIVE);




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

        var pattern = Expression.fromString("canDo(act(move, var:from, var:to))").getData();

//
//
//        assertNotNull(unify);
//        assertEquals(unify.getBoundedVariables().size(), 1);
//        assertEquals(unify.getBound("var:1"), getValue("C"));
    }
}
