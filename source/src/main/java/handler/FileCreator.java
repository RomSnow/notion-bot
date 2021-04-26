package handler;

import java.io.File;
import java.io.IOException;
import java.util.Random;

abstract class FileCreator {
    private final String name;
    private final int id;
    private final String filePath;

    public FileCreator(String name, String previousPath, FileType type) {
        this.name = name;

        int tryId = new Random().nextInt(Integer.MAX_VALUE);
        String tryFilePath = previousPath + File.separator + tryId;

        while (true) {
            try {
                createFile(tryFilePath, type);
                break;
            } catch (IOException e) {
                e.printStackTrace();
                tryId = new Random().nextInt();
                tryFilePath = previousPath + File.separator + tryId;
            }
        }

        id = tryId;
        filePath = tryFilePath;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    private void createFile(String filePath, FileType type) throws IOException {
        var file = new File(filePath);
        if (type == FileType.File) {
            if (!file.createNewFile())
                throw new IOException();
        } else {
            if (!file.mkdir())
                throw new IOException();
        }
    }
}
