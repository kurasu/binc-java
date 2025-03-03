import java.io.DataInputStream;
import java.io.IOException;

public abstract class Change
{
    protected static final int ADD_NODE = 0x01;
    protected static final int REMOVE_NODE = 0x02;
    protected static final int MOVE_NODE = 0x03;
    protected static final int SET_TYPE = 0x04;
    protected static final int DEFINE_TYPE_NAME = 0x05;
    protected static final int SET_NAME = 0x06;
    protected static final int DEFINE_ATTRIBUTE_NAME = 0x07;
    protected static final int SET_BOOL = 0x08;
    protected static final int SET_STRING = 0x09;

    public abstract int getChangeType();

    abstract void apply(Document document);

    public static Change read(DataInputStream in) throws IOException {
        final var changeType = BincIo.readLengthInverted(in);
        final var length = BincIo.readLength(in);

        return switch ((int)changeType) {
            case ADD_NODE -> AddNode.fromInput(in, length);
            case REMOVE_NODE -> RemoveNode.fromInput(in, length);
            case MOVE_NODE -> MoveNode.fromInput(in, length);
            case SET_TYPE -> SetType.fromInput(in, length);
            case DEFINE_TYPE_NAME -> DefineTypeName.fromInput(in, length);
            case SET_NAME -> SetName.fromInput(in, length);
            case DEFINE_ATTRIBUTE_NAME -> DefineAttributeName.fromInput(in, length);
            case SET_BOOL -> SetBool.fromInput(in, length);
            case SET_STRING -> SetString.fromInput(in, length);
            default -> Unknown.fromInput(in, changeType, length);
        };
    }

    static class AddNode extends Change {
        public AddNode(long id, long parent, int indexInParent) {
            this.id = id;
            this.parent = parent;
            this.indexInParent = indexInParent;
        }

        public static AddNode fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var parent = BincIo.readLength(in);
            final var index_in_parent = (int) BincIo.readLength(in);
            return new AddNode(id, parent, index_in_parent);
        }

        @Override
        public int getChangeType() {
            return ADD_NODE;
        }

        @Override
        public void apply(Document document) {
            final var node = new Node(document, this.id);
            final var parent = document.getNode(this.parent);
            node.parent = parent;
            parent.children.add(this.indexInParent, node);
        }

        private final long id;
        private final long parent;
        private final int indexInParent;
    }

    static class RemoveNode extends Change {
        public RemoveNode(long id) {
            this.id = id;
        }

        public static RemoveNode fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            return new RemoveNode(id);
        }

        @Override
        public int getChangeType() {
            return REMOVE_NODE;
        }

        @Override
        public void apply(Document document) {
            final var node = document.getNode(this.id);
            node.parent.removeChild(this.id);
        }

        private final long id;
    }

    static class MoveNode extends Change {
        public MoveNode(long id, long newParent, int indexInNewParent) {
            this.id = id;
            this.newParent = newParent;
            this.indexInNewParent = indexInNewParent;
        }

        public static MoveNode fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var newParent = BincIo.readLength(in);
            final var indexInNewParent = (int) BincIo.readLength(in);
            return new MoveNode(id, newParent, indexInNewParent);
        }

        @Override
        public int getChangeType() {
            return MOVE_NODE;
        }

        @Override
        void apply(Document document) {
            final var node = document.getNode(this.id);
            node.parent.removeChild(this.id);

            final var newParent = document.getNode(this.newParent);
            node.parent = newParent;
            newParent.children.add(this.indexInNewParent, node);
        }

        private final long id;
        private final long newParent;
        private final int indexInNewParent;
    }

    static class SetType extends Change {
        public SetType(long id, int typeId) {
            this.id = id;
            this.typeId = typeId;
        }

        @Override
        public int getChangeType() {
            return SET_TYPE;
        }

        public static SetType fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var typeId = BincIo.readLength(in);
            return new SetType(id, (int)typeId);
        }

        @Override
        void apply(Document document) {
            document.getNode(this.id).type = this.typeId;
        }

        private final long id;
        private final int typeId;
    }
    
    static class DefineTypeName extends Change {
        public DefineTypeName(int typeId, String typeName) {
            this.typeId = typeId;
            this.typeName = typeName;
        }

        @Override
        public int getChangeType() {
            return DEFINE_TYPE_NAME;
        }

        public static DefineTypeName fromInput(DataInputStream in, long length) throws IOException {
            final var typeId = BincIo.readLength(in);
            final var typeName = BincIo.readString(in);
            return new DefineTypeName((int)typeId, typeName);
        }

        @Override
        void apply(Document document) {
            document.nodeTypeNames.put(typeId, typeName);
        }

        private final int typeId;
        private final String typeName;
    }

    static class SetName extends Change {
        public SetName(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public int getChangeType() {
            return SET_NAME;
        }

        public static SetName fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var name = BincIo.readString(in);
            return new SetName(id, name);
        }

        @Override
        void apply(Document document) {
            document.getNode(this.id).name = this.name;
        }

        private final long id;
        private final String name;
    }

    static class DefineAttributeName extends Change {
        public DefineAttributeName(int attributeId, String attributeName) {
            this.attributeId = attributeId;
            this.attributeName = attributeName;
        }

        @Override
        public int getChangeType() {
            return DEFINE_ATTRIBUTE_NAME;
        }

        public static DefineAttributeName fromInput(DataInputStream in, long length) throws IOException {
            final var attrId = BincIo.readLength(in);
            final var attrName = BincIo.readString(in);
            return new DefineAttributeName((int)attrId, attrName);
        }

        @Override
        void apply(Document document) {
            document.attributeNames.put(attributeId, attributeName);
        }

        private final int attributeId;
        private final String attributeName;
    }

    static class SetBool extends Change {
        public SetBool(long id, int attributeId, boolean value) {
            this.id = id;
            this.attributeId = attributeId;
            this.value = value;
        }

        @Override
        public int getChangeType() {
            return SET_BOOL;
        }


        public static SetBool fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var attributeId = BincIo.readLength(in);
            final var value = BincIo.readBoolean(in);
            return new SetBool(id, (int)attributeId, value);
        }

        @Override
        void apply(Document document) {
            document.getNode(this.id).attributes.put(this.attributeId, value);
        }

        private final long id;
        private final int attributeId;
        private final boolean value;
    }

    static class SetString extends Change {
        public SetString(long id, int attributeId, String value) {
            this.id = id;
            this.attributeId = attributeId;
            this.value = value;
        }

        public static SetString fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var attributeId = BincIo.readLength(in);
            final var value = BincIo.readString(in);
            return new SetString(id, (int)attributeId, value);
        }

        @Override
        public int getChangeType() {
            return SET_STRING;
        }

        @Override
        void apply(Document document) {
            document.getNode(this.id).attributes.put(this.attributeId, value);
        }

        private final long id;
        private final int attributeId;
        private final String value;
    }

    static class Unknown extends Change {
        Unknown(long changeType, byte[] bytes) {
            this.changeType = changeType;
            this.bytes = bytes;
        }

        public static Change fromInput(DataInputStream in, long changeType, long length) throws IOException {
            final var bytes = new byte[(int)length];
            in.readFully(bytes);
            return new Unknown(changeType, bytes);
        }

        @Override
        public int getChangeType() {
            return (int)this.changeType;
        }

        @Override
        void apply(Document document) {

        }

        private final long changeType;
        private final byte[] bytes;
    }
}
