package handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageFile extends FileCreator{
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