package ai.prime.knowledge.nodes.binding;

import ai.prime.agent.NeuralMessage;
import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.Data;


public class QueryMessage extends NeuralMessage {
    public static final String TYPE = "queryMessage";

    private final SetMap<Data, Data> queries;

    public QueryMessage(Data from, Data to, SetMap<Data, Data> queries) {
        super(from, to);
        this.queries = queries;
    }

    public SetMap<Data, Data> getQueries() {
        return queries;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
