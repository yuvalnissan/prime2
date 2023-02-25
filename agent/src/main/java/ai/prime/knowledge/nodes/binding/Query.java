package ai.prime.knowledge.nodes.binding;

import ai.prime.knowledge.data.Data;

import java.util.Objects;

public class Query {

    private final QueryType type;
    private final Data data;

    public Query(QueryType type, Data data) {
        this.type = type;
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public QueryType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        if (query.getType() != this.getType()) {
            return false;
        }

        return Objects.equals(data, query.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
