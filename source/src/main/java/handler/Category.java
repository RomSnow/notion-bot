package handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Category extends FileCreator {
    private final HashMap<String, MessageFile> files;

    public Category(String name, String prePath) {
        super(name, prePath, FileType.Directory);
        files = new HashMap<>();
    }

    public MessageFile addFile(File file) throws IOException {
        var msgFile = new MessageFile(file.getName(), getFilePath(), FileType.File);
        files.put(msgFile.getId(), msgFile);
        msgFile.writeFile(file);
        return msgFile;
    }

    public MessageFile getFile(String fileId) {
        return files.get(fileId);
    }

    public ArrayList<MessageFile> getAllFiles() {
        return new ArrayList<>(files.values());
    }
}
