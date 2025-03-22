package binc;

import java.util.*;

public class Node {

    public Node(Document document, long id, long type) {
        this.document = document;
        this.id = id;
        this.type = type;
    }

    public Node addChild(long type) {
        return this.document.addNode(this.id, type);
    }

    public Node addChild(String typeName) {
        return this.document.addNode(this.id, typeName);
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        document.addAndApply(new Operation.SetName(this.id, name));
    }

    public long getType() {
        return this.type;
    }

    public String getTypeName() {
        return document.nodeTypeNames.getOrDefault(this.type, Long.toString(this.type));
    }

    public void setType(final long typeId) {
        document.addAndApply(new Operation.SetType(this.id, typeId));
    }

    public void setType(String typeName) {
        final var id = document.nodeTypeNames.getOrAddIdForName(typeName);
        setType(id);
    }

    public void setAttribute(long key, String value) {
        document.addAndApply(new Operation.SetString(id, key, value));
    }

    public void setAttribute(String key, String value) {
        final var id = document.attributeNames.getOrAddIdForName(key);
        setAttribute(id, value);
    }

    public void setAttribute(long key, boolean value) {
        document.addAndApply(new Operation.SetBool(id, key, value));
    }

    public void setAttribute(String key, boolean value) {
        final var id = document.attributeNames.getOrAddIdForName(key);
        setAttribute(id, value);
    }

    public String getStringAttribute(long key) {
        if (attributes.get(id) instanceof final String s) {
            return s;
        }

        return null;
    }

    public String getStringAttribute(String key) {
        final var id = document.attributeNames.getIdForName(key);
        if (id != null) {
            if (attributes.get(id) instanceof final String s) {
                return s;
            }
        }
        return null;
    }

    public Boolean getBoolAttribute(long key) {
        if (attributes.get(id) instanceof final Boolean b) {
            return b;
        }

        return null;
    }

    public Boolean getBoolAttribute(String key) {
        final var id = document.attributeNames.getIdForName(key);
        if (id != null) {
            if (attributes.get(id) instanceof final Boolean b) {
                return b;
            }
        }
        return null;
    }

    public Map<Long, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public long getId() {
        return id;
    }

    final Document document;
    final long id;
    Node parent;
    long type;
    String name;

    final List<Node> children = new ArrayList<>();
    final Map<Long, Object> attributes = new HashMap<>();
}
