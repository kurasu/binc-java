package binc;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Operation
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

    public abstract int getOperationId();

    abstract void apply(Document document);

    public static Operation read(DataInputStream in) throws IOException {
        final var operation = BincIo.readLengthInverted(in);
        final var length = BincIo.readLength(in);

        return switch ((int)operation) {
            case ADD_NODE -> AddNode.fromInput(in, length);
            case REMOVE_NODE -> RemoveNode.fromInput(in, length);
            case MOVE_NODE -> MoveNode.fromInput(in, length);
            case SET_TYPE -> SetType.fromInput(in, length);
            case DEFINE_TYPE_NAME -> DefineTypeName.fromInput(in, length);
            case SET_NAME -> SetName.fromInput(in, length);
            case DEFINE_ATTRIBUTE_NAME -> DefineAttributeName.fromInput(in, length);
            case SET_BOOL -> SetBool.fromInput(in, length);
            case SET_STRING -> SetString.fromInput(in, length);
            default -> Unknown.fromInput(in, operation, length);
        };
    }

    void write(DataOutputStream output) throws IOException {
        BincIo.writeLengthInverted(output, getOperationId());
        final var o = new ByteArrayOutputStream();
        final var out = new DataOutputStream(o);
        writeContent(out);
        final var bytes = o.toByteArray();
        BincIo.writeLength(output, bytes.length);
        output.write(bytes);
    }

    protected abstract void writeContent(DataOutputStream out) throws IOException;

    public static class AddNode extends Operation {
        public AddNode(long id, long type, long parent, int indexInParent) {
            this.id = id;
            this.type = type;
            this.parent = parent;
            this.indexInParent = indexInParent;
        }

        public static AddNode fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var type = BincIo.readLength(in);
            final var parent = BincIo.readLength(in);
            final var index_in_parent = (int) BincIo.readLength(in);
            return new AddNode(id, type, parent, index_in_parent);
        }

        @Override
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.id);
            BincIo.writeLength(out, this.type);
            BincIo.writeLength(out, this.parent);
            BincIo.writeLength(out, this.indexInParent);
        }

        @Override
        public int getOperationId() {
            return ADD_NODE;
        }

        @Override
        public void apply(Document document) {
            final var node = new Node(document, this.id, this.type);
            final var parent = document.getNode(this.parent);
            node.parent = parent;
            parent.children.add(this.indexInParent, node);
        }

        private final long id;
        private final long type;
        private final long parent;
        private final int indexInParent;
    }

    public static class RemoveNode extends Operation {
        public RemoveNode(long id) {
            this.id = id;
        }

        public static RemoveNode fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            return new RemoveNode(id);
        }

        @Override
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.id);
        }

        @Override
        public int getOperationId() {
            return REMOVE_NODE;
        }

        @Override
        public void apply(Document document) {
            final var node = document.getNode(this.id);
            node.parent.removeChild(this.id);
        }

        private final long id;
    }

    public static class MoveNode extends Operation {
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
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.id);
            BincIo.writeLength(out, this.newParent);
            BincIo.writeLength(out, this.indexInNewParent);
        }

        @Override
        public int getOperationId() {
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

    public static class SetType extends Operation {
        public SetType(long id, long typeId) {
            this.id = id;
            this.typeId = typeId;
        }

        @Override
        public int getOperationId() {
            return SET_TYPE;
        }

        public static SetType fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var typeId = BincIo.readLength(in);
            return new SetType(id, (int)typeId);
        }

        @Override
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.id);
            BincIo.writeLength(out, this.typeId);
        }

        @Override
        void apply(Document document) {
            document.getNode(this.id).type = this.typeId;
        }

        private final long id;
        private final long typeId;
    }
    
    public static class DefineTypeName extends Operation {
        public DefineTypeName(long typeId, String typeName) {
            this.typeId = typeId;
            this.typeName = typeName;
        }

        @Override
        public int getOperationId() {
            return DEFINE_TYPE_NAME;
        }

        public static DefineTypeName fromInput(DataInputStream in, long length) throws IOException {
            final var typeId = BincIo.readLength(in);
            final var typeName = BincIo.readString(in);
            return new DefineTypeName((int)typeId, typeName);
        }

        @Override
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.typeId);
            BincIo.writeString(out, this.typeName);
        }

        @Override
        void apply(Document document) {
            document.nodeTypeNames.put(typeId, typeName);
        }

        private final long typeId;
        private final String typeName;
    }

    public static class SetName extends Operation {
        public SetName(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public int getOperationId() {
            return SET_NAME;
        }

        public static SetName fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var name = BincIo.readString(in);
            return new SetName(id, name);
        }

        @Override
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.id);
            BincIo.writeString(out, this.name);
        }

        @Override
        void apply(Document document) {
            document.getNode(this.id).name = this.name;
        }

        private final long id;
        private final String name;
    }

    public static class DefineAttributeName extends Operation {
        public DefineAttributeName(long attributeId, String attributeName) {
            this.attributeId = attributeId;
            this.attributeName = attributeName;
        }

        @Override
        public int getOperationId() {
            return DEFINE_ATTRIBUTE_NAME;
        }

        public static DefineAttributeName fromInput(DataInputStream in, long length) throws IOException {
            final var attrId = BincIo.readLength(in);
            final var attrName = BincIo.readString(in);
            return new DefineAttributeName(attrId, attrName);
        }

        @Override
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.attributeId);
            BincIo.writeString(out, this.attributeName);
        }

        @Override
        void apply(Document document) {
            document.attributeNames.put(attributeId, attributeName);
        }

        private final long attributeId;
        private final String attributeName;
    }

    public static class SetBool extends Operation {
        public SetBool(long id, long attributeId, boolean value) {
            this.id = id;
            this.attributeId = attributeId;
            this.value = value;
        }

        @Override
        public int getOperationId() {
            return SET_BOOL;
        }

        public static SetBool fromInput(DataInputStream in, long length) throws IOException {
            final var id = BincIo.readLength(in);
            final var attributeId = BincIo.readLength(in);
            final var value = BincIo.readBoolean(in);
            return new SetBool(id, attributeId, value);
        }

        @Override
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.id);
            BincIo.writeLength(out, this.attributeId);
            BincIo.writeBoolean(out, this.value);
        }

        @Override
        void apply(Document document) {
            document.getNode(this.id).attributes.put(this.attributeId, value);
        }

        private final long id;
        private final long attributeId;
        private final boolean value;
    }

    public static class SetString extends Operation {
        public SetString(long id, long attributeId, String value) {
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
        protected void writeContent(DataOutputStream out) throws IOException {
            BincIo.writeLength(out, this.id);
            BincIo.writeLength(out, this.attributeId);
            BincIo.writeString(out, this.value);
        }

        @Override
        public int getOperationId() {
            return SET_STRING;
        }

        @Override
        void apply(Document document) {
            document.getNode(this.id).attributes.put(this.attributeId, value);
        }

        private final long id;
        private final long attributeId;
        private final String value;
    }

    public static class Unknown extends Operation {
        Unknown(long operationId, byte[] bytes) {
            this.operationId = operationId;
            this.bytes = bytes;
        }

        public static Operation fromInput(DataInputStream in, long operation, long length) throws IOException {
            final var bytes = new byte[(int)length];
            in.readFully(bytes);
            return new Unknown(operation, bytes);
        }

        @Override
        protected void writeContent(DataOutputStream out) throws IOException {
            out.write(this.bytes);
        }

        @Override
        public int getOperationId() {
            return (int)this.operationId;
        }

        @Override
        void apply(Document document) {

        }

        private final long operationId;
        private final byte[] bytes;
    }
}
