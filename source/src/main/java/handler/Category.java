package handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Category extends FileCreator {
    private final HashMap<String, MessageFile> files;

    public static Category restoreCategory(String id, String prePath) throws InvalidIdException {
        var categoryPath = prePath + File.separator + id;
        var listMsg = new ArrayList<MessageFile>();

        var categoryFile = new File(categoryPath);

        if (!categoryFile.isDirectory())
            throw new InvalidIdException(id);

        var filePaths = categoryFile.listFiles();

        if (filePaths != null) {
            for (var file : filePaths)
                listMsg.add(
                        MessageFile.restoreMessageFile(file.getName(), file.getParent())
                );
        }

        return new Category("category", id, prePath, listMsg);
    }

    public Category(String name, String prePath) {
        super(name, prePath, FileType.Directory);
        files = new HashMap<>();
    }

    public Category(String name, String id, String prePath, ArrayList<MessageFile> msgFiles) {
        super(name, prePath, id);
        files = new HashMap<>();

        for (var msg : msgFiles)
            files.put(msg.getId(), msg);
    }

    public MessageFile addFile(File file) throws IOException {
        var msgFile = new MessageFile(file.getName(), getFilePath(), FileType.File);
        files.put(msgFile.getId(), msgFile);
        msgFile.writeFile(file);
        return msgFile;
    }

    public MessageFile getFile(String fileId) throws InvalidIdException {
        if (!files.containsKey(fileId))
            throw new InvalidIdException(fileId);
        else
            return files.get(fileId);
    }

    public ArrayList<MessageFile> getAllFiles() {
        return new ArrayList<>(files.values());
    }
}
