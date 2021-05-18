package handler;

import db_storage.DBFileNames;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

abstract class FileCreator {
    private final String id;
    private String name;
    private final String filePath;
    protected final DBFileNames dbFileNames;

    public FileCreator(String previousPath, String id) {
        dbFileNames = new DBFileNames();
        try {
            name = dbFileNames.getNameById(id);
        } catch (InvalidIdException e) {
            name = "";
        }
        this.filePath = previousPath + File.separator + id;
        this.id = id;
    }

    public FileCreator(String name, String previousPath, FileType type) {
        this.name = name;

        String tryId = UUID.randomUUID().toString();
        String tryFilePath = previousPath + File.separator + tryId;

        while (true) {
            try {
                createFile(tryFilePath, type);
                break;
            } catch (IOException e) {
                e.printStackTrace();
                tryId = UUID.randomUUID().toString();
                tryFilePath = previousPath + File.separator + tryId;
            }
        }

        id = tryId;
        filePath = tryFilePath;
        dbFileNames = new DBFileNames();
        dbFileNames.addFileRecord(id, name, "");
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    private void createFile(String filePath, FileType type) throws IOException {
        var file = new File(filePath);
        if (type == FileType.Directory) {
            if (!file.mkdir())
                throw new IOException();
        }
    }
}
