package fr.rodofire.ewc.config;

import fr.rodofire.ewc.platform.Services;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigUtil {
    public static Path getConfigPath(String modId) {
        Path path = Services.PLATFORM.getConfigDir();
        path = path.resolve(modId);
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return path;
    }
}
