package pt.iflow.api.notification;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;

class LogCheckpoint {
    private static final String CHECKPOINT_FILE = Const.MAIL_LOG_CHECKPOINT_FILE;

    public static synchronized String loadLastTimestamp() {
        Path path = Paths.get(CHECKPOINT_FILE);
        if (Files.exists(path)) {
            try {
                byte[] bytes = Files.readAllBytes(path);
                return new String(bytes, StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                Logger.error("system", "LogCheckpoint", "loadLastTimestamp", "Failed to load checkpoint file: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public static synchronized void saveLastTimestamp(String timestamp) {
        if (timestamp == null) return;
        Path path = Paths.get(CHECKPOINT_FILE);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, timestamp.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Logger.error("system", "LogCheckpoint", "saveLastTimestamp", "Failed to save checkpoint file: " + e.getMessage(), e);
        }
    }
}
