package ezalex.manhunt_tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Path CONFIG_PATH = Path.of("config", "griefer-manhunt-tools.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config config = new Config();

    public static Config get() {
        return config;
    }

    public static void load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (!Files.exists(CONFIG_PATH)) {
                save();
                return;
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                config = GSON.fromJson(reader, Config.class);

                if (config == null) {
                    config = new Config();
                }
            }

        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to load config", e);
            config = new Config();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }

        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to save config", e);
        }
    }

    public static void set(Config newConfig) {
        config = newConfig;
        save();
    }
}
