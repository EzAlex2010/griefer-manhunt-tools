package ezalex.manhunt_tools;

import java.nio.file.Path;
import java.util.Properties;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;

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
}

/*

    private static final Properties properties = new Properties();

    public static void load() {
        try {
            // Make sure the config directory exists
            Files.createDirectories(CONFIG_PATH.getParent());

            // If the file doesn't exist, create it with defaults
            if (!Files.exists(CONFIG_PATH)) {
                properties.setProperty("challenge", "classic");
                properties.setProperty("compass_update_interval", "20");
                properties.setProperty("show_team_colors", "true");
                properties.setProperty("give_hunters_compass", "true");
                save();
                return;
            }

            // Read the file
            try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
                properties.load(in);
            }

        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to load config", e);
        }
    }
    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
                properties.store(out, "Griefer Manhunt Tools Config");
            }

        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to save config", e);
        }
    }

    public static String getChallenge() {
        return properties.getProperty("challenge");
    }
    public static void setChallenge(String challenge) {
        properties.setProperty("challenge", challenge);
    }
    public static int getCompassUpdateInterval() {
        return Integer.parseInt(properties.getProperty("compass_update_interval", "20"));
    }
    public static void setCompassUpdateInterval(int updateInterval) {
        properties.setProperty("compass_update_interval", String.valueOf(updateInterval));
    }
    public static void setGiveHuntersCompass(boolean giveHuntersCompass) {
        properties.setProperty("give_hunters_compass", String.valueOf(giveHuntersCompass));
    }
    public static boolean getGiveHuntersCompass() {
        return Boolean.parseBoolean(properties.getProperty("give_hunters_compass", "true"));
    }
    public static void setShowTeamColors(boolean showTeamColors) {
        properties.setProperty("show_team_colors", String.valueOf(showTeamColors));
    }
    public static boolean getShowTeamColors() {
        return Boolean.parseBoolean(properties.getProperty("show_team_colors", "true"));
    }
}

*/
