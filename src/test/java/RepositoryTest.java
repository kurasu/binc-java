import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void readFile() throws IOException {
        final var in = new DataInputStream(new FileInputStream("test-file.binc"));
        final var repository = Repository.read(in);
        Assertions.assertNotNull(repository);

        for (Change change : repository.changes) {
            Assertions.assertFalse(change instanceof Change.Unknown);
        }
    }
}