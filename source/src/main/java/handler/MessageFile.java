package handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageFile extends FileCreator {
    public static MessageFile restoreMessageFile(String id, String prePath) throws InvalidIdException {
        var file = new File(prePath + "/" + id);
        if (!file.isFile())
            throw new InvalidIdException(id);
        return new MessageFile(prePath, id);
    }

    public MessageFile(String previousPath, String id) {
        super(previousPath, id);
    }

    public MessageFile(String name, String previousPath, FileType type) {
        super(name, previousPath, type);
    }

    public void writeFile(File file) throws IOException {
        Files.move(Path.of(file.toString()), Paths.get(getFilePath()));
    }

    public File getFile() {
        return new File(getFilePath());
    }
}
