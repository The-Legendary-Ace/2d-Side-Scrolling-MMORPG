package io.jyberion.mmorpg.chat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChatLogger {

    private static final String LOG_DIR = "logs";  // Base log directory

    public static void logMessage(String playerName, String messageType, String message) {
        try {
            // Construct the directory path for the player and message type
            Path logDirectory = Paths.get(LOG_DIR, playerName, messageType);
            if (!Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
            }

            // Create or append to the log file named by the current date
            String logFileName = LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".log";
            File logFile = new File(logDirectory.toFile(), logFileName);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to write chat log for player " + playerName + ": " + e.getMessage());
        }
    }
}
