package binc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    public Node(Document document, long id) {
        this.document = document;
        this.id = id;
    }

    public Node addChild() {
        return this.document.addNode(this.id);
    }

    final Document document;
    final long id;
    Node parent;
    int type;
    String name;

    final List<Node> children = new ArrayList<>();
    final Map<Integer, Object> attributes = new HashMap<>();

    public Node getNode(long nodeID) {
        if (this.id == nodeID) {
            return this;
        }
        for (Node child : children) {
            final var foundNode = child.getNode(nodeID);

            if (foundNode != null) {
                return foundNode;
            }
        }
        return null;

        // or map in Doument?
    }

    public Node child(final int index) {
        return this.children.get(index);
    }

    public int childCount() {
        return this.children.size();
    }

    void removeChild(long id) {
        for (final var c : children) {
            if (c.id == id) {
                children.remove(c);
                break;
            }
        }
    }
}
