package handler;

import java.util.HashSet;
import java.util.Set;

public class UsageManager {
    private static UsageManager instance;
    private final Set<String> usingFiles;

    private UsageManager() {
        usingFiles = new HashSet<>();
    }

    public static UsageManager getInstance() {
        if (instance != null)
            return instance;
        else
            return new UsageManager();
    }

    public void tagFile(String fileId) {
        usingFiles.add(fileId);
    }

    public void untagFile(String fileId) {
        usingFiles.remove(fileId);
    }

    public boolean isFileTagged(String fileId) {
        return usingFiles.contains(fileId);
    }
}
