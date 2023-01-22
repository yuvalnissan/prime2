package ai.prime.knowledge.data;

import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.data.base.VariableData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnificationTests {

    private Expression getValue(String name) {
        return new Expression(new ValueData(name));
    }

    private Expression getVariable(String name) {
        return new Expression(new VariableData(name));
    }

    private Expression getRel(Expression from, Expression predicate, Expression to) {
        var data = new Data(new DataType("rel"), new Expression[]{
                from,
                predicate,
                to
        });

        return new Expression(data);
    }

    private Expression getInfer(Expression... targetAndConditions) {
        var data = new Data(new DataType("infer"), targetAndConditions);

        return new Expression(data);
    }

    @Test
    public void testRelationship() {
        var data = getRel(
                getValue("B"),
                getValue("isa"),
                getValue("C")
        ).getData().normalize();

        var pattern = getRel(
                getValue("B"),
                getValue("isa"),
                getVariable("4")
        ).getData().normalize();

        Unification unify = pattern.unify(data);

        assertNotNull(unify);
        assertEquals(unify.getBoundedVariables().size(), 1);
        assertEquals(unify.getBound("var:1"), getValue("C"));
    }

    @Test
    public void testNot() {
        var data = getRel(
                getValue("B"),
                getValue("isa"),
                getValue("C").not()
        ).getData().normalize();

        var pattern = getRel(
                getVariable("a").not(),
                getValue("isa"),
                getVariable("4")
        ).getData().normalize();

        Unification unify = pattern.unify(data);

        assertNotNull(unify);
        assertEquals(unify.getBoundedVariables().size(), 2);
        assertEquals(unify.getBound("var:1"), getValue("B").not());
        assertEquals(unify.getBound("var:2"), getValue("C").not());
    }

    @Test
    public void testNoneRepeating() {
        var data = getRel(
                getValue("B"),
                getValue("isa"),
                getValue("C").not()
        ).getData().normalize();

        var pattern = getRel(
                getVariable("X"),
                getValue("isa"),
                getVariable("X")
        ).getData().normalize();

        Unification unify = pattern.unify(data);

        assertNull(unify);
    }

    @Test
    public void testWrongTypeNot() {
        var data = new Data(new DataType("rel2"), new Expression[]{
                getValue("B"),
                getValue("isa"),
                getValue("C")
        });
        var pattern = getRel(
                getVariable("X").not(),
                getValue("isa"),
                getVariable("Y")
        ).getData().normalize();

        Unification unify = pattern.unify(data);

        assertNull(unify);
    }

    @Test
    public void testNesting() {
        var rel = getRel(
                getValue("S"),
                getValue("isa"),
                getValue("T")
        );
        var data = getRel(
                getValue("B"),
                getValue("isa"),
                rel
        ).getData().normalize();

        var pattern = getRel(
                getValue("B"),
                getValue("isa"),
                getVariable("4")
        ).getData().normalize();

        Unification unify = pattern.unify(data);

        assertNotNull(unify);
        assertEquals(unify.getBoundedVariables().size(), 1);
        assertEquals(unify.getBound("var:1"), rel.normalize());
    }

    @Test
    public void testInference() {
        var data = getInfer(
                getValue("Target"),
                getValue("A")
        ).getData().normalize();

        var pattern = getInfer(
                getValue("Target"),
                getVariable("X")
        ).getData().normalize();

        Unification unify = pattern.unify(data);

        assertNotNull(unify);
        assertEquals(unify.getBoundedVariables().size(), 1);
        assertEquals(unify.getBound("var:1"), getValue("A"));
    }

    @Test
    public void testFullMatch() {
        var infer = getInfer(
                getValue("Target"),
                getValue("A")
        ).normalize();
        var data = infer.getData();

        var pattern = getVariable("X").getData().normalize();

        Unification unify = pattern.unify(data);

        assertNotNull(unify);
        assertEquals(unify.getBoundedVariables().size(), 1);
        assertEquals(unify.getBound("var:1"), infer);
    }

//    @Test
//    public void testVariableConstraints() {
//
//        Data data = REL(OBJ("B"), OBJ("isa"), OBJ("C")).build().getData();
//        Data pattern = REL(VAR("X"), OBJ("isa"), VAR("Y", "X")).build().getData();
//
//        Unification unify = pattern.unify(data);
//
//        assertNotNull(unify);
//        System.out.println(unify);
//        assertEquals(unify.getBoundedVariables().size(), 2);
//        assertEquals(unify.getBound("var:1"), OBJ("B").build());
//        assertEquals(unify.getBound("var:2"), OBJ("C").build());
//
//    }
//
//
//
//
//    @Test
//    public void testInferenceWithList() {
//        Data data = INFER(AND(OBJ("a"), OBJ("c")), OBJ("b")).build().getData();
//        Data pattern = INFER(VAR("list"), VAR("x")).build().getData();
//        Unification unify = pattern.unify(data);
//
//        System.out.println(unify);
//
//        assertNotNull(unify);
//        assertEquals(unify.getBoundedVariables().size(), 2);
//        assertEquals(unify.getBound("var:1"), AND(OBJ("a"), OBJ("c")).build());
//        assertEquals(unify.getBound("var:2"), OBJ("b").build());
//
//    }
//
//    @Test
//    public void testInferenceWithSubSet() {
//
//        Data data = INFER(AND(
//                        REL(OBJ("a"), OBJ("isa"), OBJ("t")),
//                        REL(OBJ("c"), OBJ("isa"), OBJ("t"))
//                ),
//                OBJ("b")).build().getData();
//
//        Data pattern = INFER(
//                AND(
//                        REL(OBJ("a"), OBJ("isa"), VAR("T")),
//                        VAR("R")
//                ),
//                VAR("X")
//        ).build().getData();
//
//        Unification unify = pattern.unify(data);
//
//        System.out.println(unify);
//
//        assertNotNull(unify);
//        assertEquals(unify.getBoundedVariables().size(), 3);
//        assertEquals(unify.getBound("var:1"), OBJ("t").build());
//        assertEquals(unify.getBound("var:2"), REL(OBJ("c"), OBJ("isa"), OBJ("t")).build());
//        assertEquals(unify.getBound("var:3"), OBJ("b").build());
//    }
//
//    @Test
//    public void testComplexInference() {
//
//        Data data = INFER(AND(
//                        REL(OBJ("A"), OBJ("isa"), OBJ("B")),
//                        REL(OBJ("B"), OBJ("isa"), OBJ("C"))
//                ),
//                REL(OBJ("A"), OBJ("isa"), OBJ("C"))).build().getData();
//
//        Data pattern = INFER(AND(
//                        REL(VAR("a"), OBJ("isa"), OBJ("B")),
//                        VAR("y")
//                ),
//                REL(OBJ("A"), OBJ("isa"), VAR("x"))).build().getData();
//
////        Data data = typeResolver.getExpressionFromValue("((A 'isa' B), (B 'isa' C)) INFER (A 'isa' C)").getData();
////        Data pattern = typeResolver.getExpressionFromValue("((var:A 'isa' B), var:Y) INFER (A 'isa' var:X)").getData();
//        Unification unify = pattern.unify(data);
//
//        System.out.println(unify);
//
//        assertNotNull(unify);
//        assertEquals(unify.getBoundedVariables().size(), 3);
//        assertEquals(unify.getBound("var:1"), OBJ("A").build());
//        assertEquals(unify.getBound("var:2"), REL(OBJ("B"), OBJ("isa"), OBJ("C")).build());
//        assertEquals(unify.getBound("var:3"), OBJ("C").build());
//
//    }
}
