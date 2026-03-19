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
    private static final String SEPARATOR = "|";

    public static synchronized String loadLastTimestamp() {
        String raw = loadRaw();
        if (raw == null) return null;
        int sep = raw.indexOf(SEPARATOR);
        if (sep >= 0) {
            return raw.substring(0, sep);
        }
        return raw;
    }

    public static synchronized long loadLastFileSize() {
        String raw = loadRaw();
        if (raw == null) return -1;
        int sep = raw.indexOf(SEPARATOR);
        if (sep >= 0 && sep < raw.length() - 1) {
            try {
                return Long.parseLong(raw.substring(sep + 1));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static synchronized void saveCheckpoint(String timestamp, long fileSize) {
        if (timestamp == null) return;
        writeRaw(timestamp + SEPARATOR + fileSize);
    }

    public static synchronized void saveLastTimestamp(String timestamp) {
        if (timestamp == null) return;
        long existingSize = loadLastFileSize();
        if (existingSize >= 0) {
            saveCheckpoint(timestamp, existingSize);
        } else {
            writeRaw(timestamp);
        }
    }

    private static String loadRaw() {
        Path path = Paths.get(CHECKPOINT_FILE);
        if (Files.exists(path)) {
            try {
                byte[] bytes = Files.readAllBytes(path);
                return new String(bytes, StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                Logger.error("system", "LogCheckpoint", "loadRaw",
                        "Failed to load checkpoint file: " + e.getMessage(), e);
            }
        }
        return null;
    }

    private static void writeRaw(String content) {
        Path path = Paths.get(CHECKPOINT_FILE);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, content.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Logger.error("system", "LogCheckpoint", "writeRaw",
                    "Failed to save checkpoint file: " + e.getMessage(), e);
        }
    }
}
