import binc.BincIo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

class BincIoTest {

    @Test
    void writeLength() throws IOException {
        final var values = new long[]{ 20, 4, 128, 303, 999, 12323003 };
        final var bytes = new ByteArrayOutputStream();
        final var out = new DataOutputStream(bytes);

        for (long value : values) {
            BincIo.writeLength(out, value);
        }

        final var in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));

        for (long value : values) {
            Assertions.assertEquals(value, BincIo.readLength(in));
        }
    }

    @Test
    void writeLengthInverted() throws IOException {
        final var values = new long[]{ 20, 4, 128, 303, 999, 12323003 };
        final var bytes = new ByteArrayOutputStream();
        final var out = new DataOutputStream(bytes);

        for (long value : values) {
            BincIo.writeLengthInverted(out, value);
        }

        final var in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));

        for (long value : values) {
            Assertions.assertEquals(value, BincIo.readLengthInverted(in));
        }
    }

    @Test
    void writeString() throws IOException {
        final var values = new String[]{ "Hello", "", "?", "\0", "he\0llo", "😍" };
        final var bytes = new ByteArrayOutputStream();
        final var out = new DataOutputStream(bytes);

        for (final var value : values) {
            BincIo.writeString(out, value);
        }

        final var in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));

        for (final var value : values) {
            Assertions.assertEquals(value, BincIo.readString(in));
        }
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
        final var bytes = new byte[] {(byte) 0x82, 0x00 };
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
    void readLengthInverted128() throws IOException {
        final var bytes = new byte[] {(byte) 0x7E, (byte)0xFF};
        final var in = new DataInputStream(new ByteArrayInputStream(bytes));
        final var length = BincIo.readLengthInverted(in);
        Assertions.assertEquals(128, length);
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
        Assertions.assertEquals('c', fourCC & 0xff);
        Assertions.assertEquals('n', (fourCC >> 8) & 0xff);
        Assertions.assertEquals('i', (fourCC >> 16) & 0xff);
        Assertions.assertEquals('b', (fourCC >> 24) & 0xff);
    }
}