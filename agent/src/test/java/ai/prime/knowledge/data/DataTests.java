package ai.prime.knowledge.data;

import ai.prime.knowledge.data.base.ValueData;
import ai.prime.knowledge.data.base.VariableData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataTests {

    private Expression getValueExpression(String name) {
        return new Expression(new ValueData(name));
    }

    private Expression getVariableExpression(String name) {
        return new Expression(new VariableData(name));
    }

    @Test
    public void normalizeNoVariables() {
        var data = new Data(new DataType("rel"), new Expression[]{
                getValueExpression("A"),
                getValueExpression("isa"),
                getValueExpression("B")
        });

        assertEquals(data, data.normalize());
    }

    @Test
    public void normalizeWithVariables() {
        var data = new Data(new DataType("rel"), new Expression[]{
                getVariableExpression("X"),
                getValueExpression("isa"),
                getVariableExpression("Y")
        });

        assertEquals(data.getDisplayName(), "rel(var:X,isa,var:Y)");

        var normalized = data.normalize();
        assertEquals(normalized.getDisplayName(), "rel(var:1,isa,var:2)");
    }
}
