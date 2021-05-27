package handler;

import java.util.HashMap;
import java.util.Map;

public class UsageManager {
    private static UsageManager instance;
    private final Map<String, Integer> usingFiles;

    private UsageManager() {
        usingFiles = new HashMap<>();
    }

    public static UsageManager getInstance() {
        if (instance == null)
            instance = new UsageManager();

        return instance;
    }

    public void tagFile(String fileId) {
        if (usingFiles.containsKey(fileId))
            usingFiles.put(fileId, usingFiles.get(fileId) + 1);
        else
            usingFiles.put(fileId, 1);
    }

    public void untagFile(String fileId) {
        usingFiles.put(fileId, usingFiles.get(fileId) - 1);
    }

    public boolean isFileTagged(String fileId) {
        if (usingFiles.containsKey(fileId))
            return usingFiles.get(fileId) > 0;

        return false;
    }
}
