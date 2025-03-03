import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BincIo {
    public static void writeLength(DataOutputStream out, long value) throws IOException {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
        while ((value & ~0x7FL) != 0L) {
            out.writeByte((int) ((value & 0x7F) | 0x80));
            value >>>= 7;
        }
        out.writeByte((int) value);
    }


    public static void writeLengthInverted(DataOutputStream out, long value) throws IOException {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
        while ((value & ~0x7FL) != 0L) {
            out.writeByte((int) (((value & 0x7F) ^ 0x7F) | 0x80));
            value >>>= 7;
        }
        out.writeByte((int) value);
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
        long value = 0L;
        int shift = 0;
        byte b;
        do {
            b = in.readByte();
            value |= (long) (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return value;
    }

    public static long readLengthInverted(DataInputStream in) throws IOException {
        long value = 0L;
        int shift = 0;
        byte b;
        do {
            b = in.readByte();
            value |= (long) ((b ^ 0x7F) & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) == 0);
        return value;
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
