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

        return new Category(id, prePath, listMsg);
    }

    public Category(String name, String prePath) {
        super(name, prePath, FileType.Directory);
        files = new HashMap<>();
    }

    public Category(String id, String prePath, ArrayList<MessageFile> msgFiles) {
        super(prePath, id);
        files = new HashMap<>();

        for (var msg : msgFiles)
            files.put(msg.getId(), msg);
    }

    public MessageFile addFile(File file) throws IOException {
        var msgFile = new MessageFile(file.getName(), getFilePath(), FileType.File);
        files.put(msgFile.getId(), msgFile);
        usageManager.tagFile(msgFile.getId());
        msgFile.writeFile(file);
        return msgFile;
    }

    public MessageFile addFile(File file, String fileName) throws IOException {
        var msgFile = new MessageFile(fileName, getFilePath(), FileType.File);
        files.put(msgFile.getId(), msgFile);
        usageManager.tagFile(msgFile.getId());
        msgFile.writeFile(file);
        return msgFile;
    }

    public MessageFile getFileById(String fileId) throws InvalidIdException {
        if (!files.containsKey(fileId))
            throw new InvalidIdException(fileId);
        else {
            usageManager.tagFile(fileId);
            return files.get(fileId);
        }
    }

    public MessageFile getFileByName(String name) throws InvalidIdException {
        var id = dbFileNames.getIdByName(name);
        return getFileById(id);
    }

    public void removeFileMessageById(String id) throws BusyException {
        Deleter.tryToGetAccess(id, usageManager);
        var msg = files.get(id);
        var filePath = msg.getFilePath();
        var file = new File(filePath);
        file.delete();
        files.remove(id);
        try {
            dbFileNames.removeFileRecordById(id);
        } catch (InvalidIdException ignore) {}
    }

    public void removeFileMessage(String name) throws InvalidIdException, BusyException {
        var id = dbFileNames.getIdByName(name);
        removeFileMessageById(id);
    }

    public ArrayList<MessageFile> getAllFiles() {
        return new ArrayList<>(files.values());
    }
}
