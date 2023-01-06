package ai.prime.server.models;

import java.util.Map;

public class NodeModel {
    private final Map<String, String> props;

    public NodeModel(Map<String, String> props) {
        this.props = props;
    }

    public Map<String, String> getProps() {
        return props;
    }
}
