import binc.BincIo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

class BincIoTest {

    final long[] values = new long[]{20, 4, 128, 303, 999, 12323003, 0, 1, 9, 127, 128, 129, 218, 219, 220, 254, 255,
            256, 267, 333, 513, 1000, 8410, 8411, 8412, 10000, 73946, 73947, 73948, 100000, 16777215, 16777216,
            1000000000, 10000001000000000L};

    final long[] valuesAndLengths = new long[] {
            0, 1,
            219, 1,
            220, 2,
            8410, 2,
            8411, 3,
            65536 + 8410, 3,
            65536 + 8411, 4,
            16777215, 4,
            16777216, 5,
            0xFFFFFFFFL, 5,
            0x100000000L, 9,
            0xFFFFFFFFFFFFFFFFL, 9};

    @Test
    void writeLength() throws IOException {

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
    void writeLengthByteLength() throws IOException {
        int n = valuesAndLengths.length / 2;
        for (int i = 0; i < n; i+=2) {
            final long value  = valuesAndLengths[i];
            final long size = valuesAndLengths[i+1];
            final var bytes = new ByteArrayOutputStream();
            final var out = new DataOutputStream(bytes);
            BincIo.writeLength(out, value);
            Assertions.assertEquals(size, bytes.toByteArray().length);
        }
    }
    @Test
    void writeLengthInvertedByteLength() throws IOException {
        int n = valuesAndLengths.length / 2;
        for (int i = 0; i < n; i+=2) {
            final long value  = valuesAndLengths[i];
            final long size = valuesAndLengths[i+1];
            final var bytes = new ByteArrayOutputStream();
            final var out = new DataOutputStream(bytes);
            BincIo.writeLengthInverted(out, value);
            Assertions.assertEquals(size, bytes.toByteArray().length);
        }
    }

    @Test
    void writeLengthInverted() throws IOException {
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
        final var bytes = new byte[] {(byte) 0xDC, 0x25 };
        final var in = new DataInputStream(new ByteArrayInputStream(bytes));
        final var length = BincIo.readLength(in);
        Assertions.assertEquals(256, length);
    }

    @Test
    void readLengthInverted23() throws IOException {
        final var bytes = new byte[] {(byte)(23 ^ 0xFF)};
        final var in = new DataInputStream(new ByteArrayInputStream(bytes));
        final var length = BincIo.readLengthInverted(in);
        Assertions.assertEquals(23, length);
    }
    @Test
    void readLengthInverted256() throws IOException {
        final var bytes = new byte[] {~(byte)0xDC, ~(byte)0x25};
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
        Assertions.assertEquals('c', fourCC & 0xFF);
        Assertions.assertEquals('n', (fourCC >> 8) & 0xFF);
        Assertions.assertEquals('i', (fourCC >> 16) & 0xFF);
        Assertions.assertEquals('b', (fourCC >> 24) & 0xFF);
    }
}