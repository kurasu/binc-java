import binc.Document;
import binc.Journal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

class DocumentTest {

    @Test
    void readFile() throws IOException {
        final var in = new DataInputStream(new FileInputStream("test-file.binc"));
        final var journal = Journal.read(in);
        Assertions.assertNotNull(journal);
        final var document = new Document(journal);
        Assertions.assertNotNull(document);
        Assertions.assertEquals(2, document.root().childCount());
        Assertions.assertEquals(0, document.root().child(0).childCount());
        Assertions.assertEquals(1, document.root().child(1).childCount());
    }

    @Test
    void create() {
        final var document = new Document();
        final var n1 = document.root().addChild(0);
        final var n2 = document.root().addChild(0);
        final var n3 = n2.addChild(0);

        Assertions.assertEquals(2, document.root().childCount());
        Assertions.assertEquals(0, document.root().child(0).childCount());
        Assertions.assertEquals(1, document.root().child(1).childCount());
    }

    @Test
    void write() throws IOException {
        final var document = new Document();
        final var n1 = document.root().addChild(0);
        final var n2 = document.root().addChild(0);
        final var n3 = n2.addChild(1);

        final var out = new ByteArrayOutputStream();
        document.write(new DataOutputStream(out));

        final var input = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        final var journal = Journal.read(input);
        Assertions.assertNotNull(journal);
        Assertions.assertEquals(document.getJournal().getOperations().size(), journal.getOperations().size());
        final var readDocument = new Document(journal);
        Assertions.assertNotNull(readDocument);
        Assertions.assertEquals(document.root().childCount(), readDocument.root().childCount());
    }

    @Test
    void writeComplex() throws IOException {
        final var document = new Document();
        final var root = document.root();
        final var mimeType = "application/book-store";
        root.setAttribute("mime-type", mimeType);

        final var n1 = root.addChild("shelf");
        final var n2 = root.addChild("shelf");
        final var n3 = n2.addChild("book");

        n1.setName("My favorites");
        n2.setName("To write");
        n2.setName("binc manual");
        n2.setAttribute("chapters", "none");

        final var out = new ByteArrayOutputStream();
        document.write(new DataOutputStream(out));

        final var input = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        final var journal = Journal.read(input);
        Assertions.assertNotNull(journal);
        Assertions.assertEquals(document.getJournal().getOperations().size(), journal.getOperations().size());
        final var readDocument = new Document(journal);
        Assertions.assertNotNull(readDocument);
        Assertions.assertEquals(root.childCount(), readDocument.root().childCount());

        Assertions.assertEquals("book", readDocument.getNode(n3.getId()).getTypeName());

        Assertions.assertEquals(mimeType, readDocument.root().getStringAttribute("mime-type"));
    }
}