package ai.prime.agent;

import ai.prime.common.utils.SetMap;
import ai.prime.knowledge.data.DataType;
import ai.prime.knowledge.nodes.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NodeMapping {
    private final SetMap<DataType, Class<Node>> dataToNodes;
    private final Set<Class<Node>> defaultNodes;

    public NodeMapping() {
        this.dataToNodes = new SetMap<>();
        this.defaultNodes = new HashSet<>();
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

    public Collection<Class<Node>> getNodesForType(DataType dataType) {
        Collection<Class<Node>> nodeClasses = new HashSet<>();
        nodeClasses.addAll(defaultNodes);
        nodeClasses.addAll(dataToNodes.getValues(dataType));

        return nodeClasses;
    }
}
