package ai.prime.knowledge.nodes.binding;

import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.Data;


public class QueryMessage extends NeuralMessage {
    public static final String TYPE = "queryMessage";

    private final SetMap<Query, Data> queries;

    public QueryMessage(Data from, Data to, SetMap<Query, Data> queries) {
        super(from, to);
        this.queries = queries;
    }

    public SetMap<Query, Data> getQueries() {
        return queries;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
