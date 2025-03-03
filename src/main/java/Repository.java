import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    final List<Change> changes = new ArrayList<>();

    public static Repository read(DataInputStream in) throws IOException {
        final var repo = new Repository();
        final var bincID = in.readInt();
        final var bincVersion = in.readInt();

        if (bincID != BincIo.toFourCC("binc"))
            throw new IOException("binc header expected here");
        if (bincVersion >= 1)
            throw new IOException("Unsupported version");

        try {
            while (true) {
                final var change = Change.read(in);
                repo.changes.add(change);
            }
        }
        catch (EOFException e) {
           return repo;
        }
        catch (IOException e) {
            throw new IOException(e);
        }
    }
}
