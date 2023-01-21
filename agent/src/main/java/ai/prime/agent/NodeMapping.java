package ai.prime.agent;

import ai.prime.common.utils.ListMap;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.nodes.Node;

import java.util.LinkedList;
import java.util.List;

public class NodeMapping {
    private final ListMap<DataType, Class<Node>> dataToNodes;
    private final List<Class<Node>> defaultNodes;

    public NodeMapping() {
        this.dataToNodes = new ListMap<>();
        this.defaultNodes = new LinkedList<>();
    }

    private Class<Node> getClass(String nodeClassName) {
        try {
            Class<Node> nodeClass = (Class<Node>) Class.forName(nodeClassName);

            return nodeClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerDefaultNode(String nodeClassName) {
        Class<Node> nodeClass = getClass(nodeClassName);
        defaultNodes.add(nodeClass);
    }

    public void registerDataToNode(DataType dataType, String nodeClassName) {
        Class<Node> nodeClass = getClass(nodeClassName);
        dataToNodes.add(dataType, nodeClass);
    }

    public List<Class<Node>> getNodesForType(DataType dataType) {
        List<Class<Node>> nodeClasses = new LinkedList<>();
        nodeClasses.addAll(defaultNodes);
        nodeClasses.addAll(dataToNodes.getValues(dataType));

        return nodeClasses;
    }
}
