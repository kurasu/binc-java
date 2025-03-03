import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

class DocumentTest {

    @Test
    void readFile() throws IOException {
        final var in = new DataInputStream(new FileInputStream("test-file.binc"));
        final var repository = Repository.read(in);
        Assertions.assertNotNull(repository);
        final var document = new Document(repository);
        Assertions.assertNotNull(document);
        Assertions.assertEquals(2, document.root().children.size());
        Assertions.assertEquals(0, document.root().children.get(0).children.size());
        Assertions.assertEquals(1, document.root().children.get(1).children.size());
    }

    @Test
    void create() throws IOException {
        final var document = new Document();
        final var n1 = document.root().addChild();
        final var n2 = document.root().addChild();
        final var n3 = n2.addChild();

        Assertions.assertEquals(2, document.root().children.size());
        Assertions.assertEquals(0, document.root().children.get(0).children.size());
        Assertions.assertEquals(1, document.root().children.get(1).children.size());
    }
}