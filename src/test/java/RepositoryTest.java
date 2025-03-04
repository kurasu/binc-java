import binc.Change;
import binc.Repository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

class RepositoryTest {

    @Test
    void readFile() throws IOException {
        final var in = new DataInputStream(new FileInputStream("test-file.binc"));
        final var repository = Repository.read(in);
        Assertions.assertNotNull(repository);

        for (Change change : repository.getChanges()) {
            Assertions.assertFalse(change instanceof Change.Unknown);
        }
    }

    @Test
    void writeFile() throws IOException {
        final var in = new DataInputStream(new FileInputStream("test-file.binc"));
        final var repository = Repository.read(in);
        Assertions.assertNotNull(repository);

        final var out = new ByteArrayOutputStream();
        repository.write(new DataOutputStream(out));
        final var reference = new DataInputStream(new FileInputStream("test-file.binc")).readAllBytes();
        Assertions.assertArrayEquals(reference, out.toByteArray());
    }
}