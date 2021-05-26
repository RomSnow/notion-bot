package handler;

import db_storage.DBFileNames;

import java.io.File;

public class Deleter {
    public static void tryToGetAccess(String id, UsageManager usageManager) throws BusyException {
        var tCount = 0;
        while (usageManager.isFileTagged(id)) {
            if (tCount > 5)
                throw new BusyException("File is busy");

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}

            tCount += 1;
        }
    }

    public static void deleteDirectory(File directoryToBeDeleted, DBFileNames db) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file, db);
                try {
                    db.removeFileRecordById(file.getName());
                } catch (InvalidIdException e) {
                    continue;
                }
            }
        }
        try {
            db.removeFileRecordById(directoryToBeDeleted.getName());
        } catch (InvalidIdException ignored) {}

        directoryToBeDeleted.delete();
    }
}
