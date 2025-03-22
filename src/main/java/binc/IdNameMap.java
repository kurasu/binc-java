package binc;

import java.util.HashMap;

public abstract class IdNameMap extends HashMap<Long, String> {

    protected IdNameMap(final Document document) {
        this.document = document;
    }

    public Long getIdForName(String name){
        for (final var entry : entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    long getOrAddIdForName(String name){
        final var id = getIdForName(name);
        if (id == null) {
            while (get(nextId) != null) {
                nextId++;
            }
            defineIdName(nextId, name);
            return nextId;
        }
        return id;
    }

    protected abstract void defineIdName(long id, String name);

    static class TypeNameMap extends IdNameMap {
        public TypeNameMap(Document document) {
            super(document);
        }

        @Override
        protected void defineIdName(long id, String name) {
            document.addAndApply(new Operation.DefineTypeName(id, name));
        }
    }

    static class AttributeNameMap extends IdNameMap {
        public AttributeNameMap(Document document) {
            super(document);
        }

        @Override
        protected void defineIdName(long id, String name) {
            document.addAndApply(new Operation.DefineAttributeName(id, name));
        }
    }

    private long nextId = 1;
    protected final Document document;
}
