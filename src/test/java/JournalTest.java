import binc.Operation;
import binc.Journal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

class JournalTest {

    @Test
    void readFile() throws IOException {
        final var in = new DataInputStream(new FileInputStream("test-file.binc"));
        final var journal = Journal.read(in);
        Assertions.assertNotNull(journal);

        for (Operation operation : journal.getOperations()) {
            Assertions.assertFalse(operation instanceof Operation.Unknown);
        }
    }

    @Test
    void writeFile() throws IOException {
        final var in = new DataInputStream(new FileInputStream("test-file.binc"));
        final var journal = Journal.read(in);
        Assertions.assertNotNull(journal);

        final var out = new ByteArrayOutputStream();
        journal.write(new DataOutputStream(out));
        final var reference = new DataInputStream(new FileInputStream("test-file.binc")).readAllBytes();
        Assertions.assertArrayEquals(reference, out.toByteArray());
    }
}