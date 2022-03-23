import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class NodeGetChild extends RecursiveTask<Set<Node>> {
    private Node node;

    public NodeGetChild(Node node) {
            this.node = node;
        node.makeChildren();
    }

    @Override
    protected Set<Node> compute() {
        HashSet<Node> result = new HashSet<>();
        result.addAll(node.getChildNods());
        List<NodeGetChild> nodeGetChildren = new ArrayList<>();
        for (Node element : node.getChildNods()) {
            NodeGetChild task = new NodeGetChild(element);
            task.fork();
            nodeGetChildren.add(task);
        }
        for (NodeGetChild task : nodeGetChildren) {
            result.addAll(task.join());
        }
        return result;
    }
}


