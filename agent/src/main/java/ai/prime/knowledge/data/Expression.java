package ai.prime.knowledge.data;

public class Expression {
    private Data data;
    private DataModifier modifier;
    private String displayName = null;
    private int hashCode = 0;

    public Expression(Data data, DataModifier modifier) {
        this.data = data;
        this.modifier = modifier;
    }

    public Expression(Data data) {
        this(data, DataModifier.POSITIVE);
    }

    public Data getData() {
        return data;
    }

    public DataModifier getModifier() {
        return modifier;
    }

    public String getDisplayName() {
        if (displayName == null) {
            String dataDisplayName = data.getDisplayName();
            if (modifier == DataModifier.POSITIVE) {
                displayName = dataDisplayName;
            } else {
                displayName = modifier.getDisplayName() + NamingUtil.START_COMPLEX_EXPRESSION + dataDisplayName + NamingUtil.END_COMPLEX_EXPRESSION;
            }
        }

        return displayName;
    }

    public int hashCode(){
        if (hashCode == 0){
            hashCode = getDisplayName().hashCode();
        }

        return hashCode;
    }
}
