package ai.prime.knowledge.nodes.binding;

import ai.prime.knowledge.data.Data;
import ai.prime.knowledge.data.Unification;

public class BindingMatch {
    private final Data query;
    private final Data source;
    private final Data match;
    private final Unification binding;

    public BindingMatch(Data query, Data source, Data match, Unification binding) {
        this.query = query;
        this.source = source;
        this.match = match;
        this.binding = binding;
    }

    public Data getQuery() {
        return query;
    }

    public Data getSource() {
        return source;
    }

    public Data getMatch() {
        return match;
    }

    public Unification getBinding() {
        return binding;
    }

    @Override
    public String toString() {
        return "BindingMatch{" +
                "query=" + query +
                ", source=" + source +
                ", match=" + match +
                ", binding=" + binding +
                '}';
    }
}
