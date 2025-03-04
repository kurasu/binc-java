package binc;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Document
{
    public static final long ROOT_ID = 0;

    public Document() {
        repository = new Repository();
    }

    public Document(final Repository repository) {
        this.repository = repository;
        recreate();
    }

    public Node addNode(final long parentID) {
        final var id = getNextId();
        final var parent = getNode(parentID);
        final var msg = new Change.AddNode(id, parentID, parent.children.size());
        addAndApply(msg);
        return getNode(id);
    }

    public Node root()
    {
        return rootNode;
    }

    public Node getNode(long nodeID) {
        return rootNode.getNode(nodeID);
    }

    private void addAndApply(final Change change) {
        repository.changes.add(change);
        change.apply(this);
    }

    long getNextId() {
        return nextId++;
    }

    private void recreate() {
        nodeTypeNames.clear();
        attributeNames.clear();
        rootNode = new Node(this, ROOT_ID);

        for (Change change : repository.changes) {
            change.apply(this);
        }
    }

    private Node rootNode = new Node(this, ROOT_ID);

    private final Repository repository;

    private long nextId = ROOT_ID+1;

    public Map<Integer, String> nodeTypeNames = new HashMap<>();
    public Map<Integer, String> attributeNames = new HashMap<>();

    public void write(DataOutputStream out) throws IOException {
        repository.write(out);
    }

    public Repository getRepository() {
        return repository;
    }
}
