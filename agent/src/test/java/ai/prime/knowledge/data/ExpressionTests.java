package ai.prime.knowledge.data;

import ai.prime.knowledge.data.base.VariableData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionTests {

    @Test
    public void getExpressionFromString() {
        var id = "rel(A, B)";
        Expression expression = Expression.fromString(id);

        assertEquals(expression.getDisplayName(), "rel(A,B)");
    }

    @Test
    public void invalidFormat() {
        var id = "rel(A, (B)";
        assertThrows(RuntimeException.class, () -> Expression.fromString(id));
    }

    @Test
    public void getExpressionFromStringWithModifier() {
        var id = "rel(A, NOT(  B   ))";
        Expression expression = Expression.fromString(id);

        assertEquals(expression.getDisplayName(), "rel(A,NOT(B))");
        assertEquals(expression.getData().getExpressions()[1].getModifier(), DataModifier.NEGATIVE);
    }

    @Test
    public void getVar() {
        var id = "rel(A, rel(var: X, B))";
        Expression expression = Expression.fromString(id);

        assertEquals(expression.getDisplayName(), "rel(A,rel(var:X,B))");
        assertEquals(expression.getData().getExpressions()[1].getData().getExpressions()[0].getData().getType(), VariableData.TYPE);
    }
}
