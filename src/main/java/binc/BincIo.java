package binc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BincIo {
    public static void writeLength(DataOutputStream out, long value) throws IOException {
        final long T1 = 204;
        final long T2 = 32 * 256 + T1;
        final var T3 = 16 * 65536 + T2;

        if (value <= T1) {
            out.writeByte((int) value);
        } else if (value < T2) {
            out.writeByte((int) (((value - T1) >> 8) + T1 + 1));
            out.writeByte((int) ((value - T1) & 0xFF));
        } else if (value < T3){
            out.writeByte((int) (237 + ((value - T2) >> 16)));
            out.writeShort((int) (value - T2) & 0xFFFF);
        } else if (value < 16777216) {
            out.writeByte(253);
            out.writeByte((int) (value >> 16)& 0xFF);
            out.writeByte((int) (value >> 8)& 0xFF);
            out.writeByte((int) value & 0xFF);
        } else if (value <= 0x7fffffffL) {
            out.writeByte(254);
            out.writeInt((int) value);
        } else {
            out.writeByte(255);
            out.writeLong(value);
        }
    }

    public static void writeLengthInverted(DataOutputStream out, long value) throws IOException {
        final long T1 = 204;
        final long T2 = 32 * 256 + T1;
        final var T3 = 16 * 65536 + T2;

        if (value <= T1) {
            out.writeByte((int) ~value);
        } else if (value < T2) {
            out.writeByte((int) ~(((value - T1) >> 8) + T1 + 1));
            out.writeByte((int) ~((value - T1) & 0xFF));
        } else if (value < T3){
            out.writeByte(~((int) (237 + ((value - T2) >> 16))));
            out.writeShort(~((int) (value - T2)) & 0xFFFF);
        } else if (value < 16777216) {
            out.writeByte(~253);
            out.writeByte(~((int) (value >> 16) & 0xFF));
            out.writeByte(~((int) (value >> 8) & 0xFF));
            out.writeByte(~((int) value & 0xFF));
        } else if (value <= 0x7fffffffL) {
            out.writeByte(~254);
            out.writeInt(~((int) value));
        } else {
            out.writeByte(~255);
            out.writeLong(~value);
        }
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
        final long T1 = 204;
        final long T11 = 205;
        final long T2 = 32 * 256 + T1;

        int a0 = in.readUnsignedByte();

        if (a0 < 205) {
            return a0;
        } else if (a0 < 237){
            long a1 = in.readUnsignedByte();
            return ((a0 - T11) << 8 | a1) + T1;
        } else if (a0 < 253) {
            long a1 = in.readUnsignedShort();
            return a1 + T2 + ((a0 - 237) << 16);
        } else if (a0 == 253) {
            long a1 = in.readUnsignedByte();
            long a2 = in.readUnsignedByte();
            long a3 = in.readUnsignedByte();
            return a1 <<  16 | (a2 << 8) | a3;
        } else if (a0 == 254) {
            return in.readInt() & 0xFFFFFFFFL;
        } else { // 255
            return in.readLong();
        }
    }

    public static long readLengthInverted(DataInputStream in) throws IOException {
        final long T1 = 204;
        final long T11 = 205;
        final long T2 = 32 * 256 + T1;

        int a0 = in.readUnsignedByte() ^ 0xFF;

        if (a0 < 205) {
            return a0;
        } else if (a0 < 237){
            long a1 = in.readUnsignedByte() ^ 0xFF;
            return ((a0 - T11) << 8 | a1) + T1;
        } else if (a0 < 253) {
            long a1 = in.readUnsignedShort() ^ 0xFFFF;
            return a1 + T2 + ((a0 - 237) << 16);
        } else if (a0 == 253) {
            long a1 = in.readUnsignedByte() ^ 0xFF;
            long a2 = in.readUnsignedByte() ^ 0xFF;
            long a3 = in.readUnsignedByte() ^ 0xFF;
            return a1 <<  16 | (a2 << 8) | a3;
        } else if (a0 == 254) {
            return ~in.readInt() & 0xFFFFFFFFL;
        } else { // 255
            return ~in.readLong();
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
