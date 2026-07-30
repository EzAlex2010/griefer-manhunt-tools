package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.Config;
import net.minecraft.client.gui.screens.Screen;

public class ServerConfigCopy {

    private static Config config = new Config();

    public static Screen parentScreen;
    public static String challenge;

    public static void set(Config newConfig) {
        config = newConfig;
    }

    public static Config get() {
        return config;
    }
}
