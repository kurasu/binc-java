package binc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BincIo {
    public static void writeLength(DataOutputStream out, long value) throws IOException {
        boolean wrote = false;
        for (int i = 0; i <= 8; i++) {
            final long x = (value >> (7 * (9 - i))) & 0x7f;
            if (wrote || x != 0) {
                out.writeByte((int) (x | 0x80));
                wrote = true;
            }
        }
        out.writeByte((int) (value & 0x7f));
    }


    public static void writeLengthInverted(DataOutputStream out, long value) throws IOException {
        boolean wrote = false;
        for (int i = 0; i <= 8; i++) {
            final long x = (value >> (7 * (9 - i))) & 0x7f;
            if (wrote || x != 0) {
                out.writeByte((int) (x | 0x80) ^ 0xFF);
                wrote = true;
            }
        }
        out.writeByte((int) (value & 0x7f) ^ 0xFF);
    }

    public static void writeString(DataOutputStream out, String value) throws IOException {
        final var bytes = value.getBytes(StandardCharsets.UTF_8);
        writeLength(out, bytes.length);
        out.write(bytes);
    }

    public static void writeBoolean(DataOutputStream out, boolean value) throws IOException {
        out.writeBoolean(value);
    }

    public static long readLength(DataInputStream in) throws IOException {
        long value = 0;
        while (true)
        {
            value = value << 7;
            int b = in.readUnsignedByte();
            value |= (b & 0x7F);
            if ((b & 0x80) == 0) {
                return value;
            }
        }
    }

    public static long readLengthInverted(DataInputStream in) throws IOException {
        long value = 0;
        while (true)
        {
            value = value << 7;
            int b = in.readUnsignedByte() ^ 0xFF;
            value |= (b & 0x7F);
            if ((b & 0x80) == 0) {
                return value;
            }
        }
    }

    public static String readString(DataInputStream in) throws IOException {
        final var length = (int)readLength(in);
        final var bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static boolean readBoolean(DataInputStream in) throws IOException {
        return in.readBoolean();
    }

    public static int toFourCC(String code) {
        if (code.length() != 4) {
            throw new IllegalArgumentException("FourCC code must be 4 characters long");
        }
        return ((code.charAt(0) & 0xFF) << 24) |
                ((code.charAt(1) & 0xFF) << 16) |
                ((code.charAt(2) & 0xFF) << 8) |
                (code.charAt(3) & 0xFF);
    }
}
