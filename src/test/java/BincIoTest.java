import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BincIoTest {

    @Test
    void writeLength() {
    }

    @Test
    void writeLengthInverted() {
    }

    @Test
    void writeString() {
    }

    @Test
    void writeBoolean() {
    }

    @Test
    void readLength95() throws IOException {
        final var bytes = new byte[] { 95 };
        final var in = new DataInputStream(new ByteArrayInputStream(bytes));
        final var length = BincIo.readLength(in);
        Assertions.assertEquals(95, length);
    }

    @Test
    void readLength256() throws IOException {
        final var bytes = new byte[] {(byte) 0x81, 0x00 };
        final var in = new DataInputStream(new ByteArrayInputStream(bytes));
        final var length = BincIo.readLength(in);
        Assertions.assertEquals(256, length);
    }

    @Test
    void readLengthInverted23() throws IOException {
        final var bytes = new byte[] {(byte)(23 ^ 0xff)};
        final var in = new DataInputStream(new ByteArrayInputStream(bytes));
        final var length = BincIo.readLengthInverted(in);
        Assertions.assertEquals(23, length);
    }
    @Test
    void readLengthInverted256() throws IOException {
        final var bytes = new byte[] {(byte) 0x7E, (byte)0xFF};
        final var in = new DataInputStream(new ByteArrayInputStream(bytes));
        final var length = BincIo.readLengthInverted(in);
        Assertions.assertEquals(256, length);
    }

    @Test
    void readString() throws IOException {
        final var bytes = new byte[] { 0x3, 'a', 'b', 'c'};
        final var in = new DataInputStream(new ByteArrayInputStream(bytes));
        final var string = BincIo.readString(in);
        Assertions.assertEquals("abc", string);
    }

    @Test
    void toFourCC() {
        final var fourCC = BincIo.toFourCC("binc");
        Assertions.assertEquals(fourCC & 0xff, 'c');
        Assertions.assertEquals((fourCC >> 8) & 0xff, 'n');
        Assertions.assertEquals((fourCC >> 16) & 0xff, 'i');
        Assertions.assertEquals((fourCC >> 24) & 0xff, 'b');
    }
}