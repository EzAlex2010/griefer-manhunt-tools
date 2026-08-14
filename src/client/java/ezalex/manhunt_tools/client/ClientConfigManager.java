package ezalex.manhunt_tools.client;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

public class ClientConfigManager {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("griefer-manhunt-tools-client.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ClientConfig config;

    public static ClientConfig get() {
        return config;
    }

    public static void save() {
        try {
            Files.writeString(
                    CONFIG_PATH,
                    GSON.toJson(config)
            );
        } catch (IOException e) {
            GrieferManhuntToolsClient.LOGGER.error("Couldn't save griefer-manhunt-tools-client.json", e);
        }
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            config = new ClientConfig();
            save();
            return;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            config = GSON.fromJson(json, ClientConfig.class);
            if (config == null) { // Protect against a null result from malformed/empty JSON
                config = new ClientConfig();
            }
        } catch (IOException e) {
            GrieferManhuntToolsClient.LOGGER.error("Couldn't load griefer-manhunt-tools-client.json", e);

            config = new ClientConfig();
        }
    }
}
