package com.academic.PcShop;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;

@SpringBootApplication
public class PcShopApplication {
    private static final String LOG_DIR = "logs/";
    private static final Logger logger = LoggerFactory.getLogger(PcShopApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PcShopApplication.class, args);
    }

    @PostConstruct
    private void init() {
        File logDir = new File(LOG_DIR);
        if (isValidDirectory(logDir)) {
            File[] logFiles = getLogFiles(logDir);
            if (logFiles != null) {
                clearLogFiles(logFiles);
            } else {
                logDirectoryError(logDir);
            }
        } else {
            logDirectoryError(logDir);
        }
    }

    private static boolean isValidDirectory(File directory) {
        return directory.exists() && directory.isDirectory();
    }

    private static File[] getLogFiles(File directory) {
        return directory.listFiles((dir, name) -> name.endsWith(".log"));
    }

    private static void clearLogFiles(File[] logFiles) {
        for (File file : logFiles) {
            if (file.isFile()) {
                try (FileWriter writer = new FileWriter(file, false)) {
                    writer.write(""); // Clears the file contents
                    logger.info("Cleared contents of: {}", file.getAbsolutePath());
                } catch (IOException e) {
                    logger.error("Failed to clear contents of: {}", file.getAbsolutePath());
                }
            }
        }
    }

    private static void logDirectoryError(File directory) {
        logger.error("Failed to process directory: {}", directory.getAbsolutePath());
    }

}
