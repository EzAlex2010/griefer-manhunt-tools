package ezalex.manhunt_tools.challenges;

import net.minecraft.server.MinecraftServer;

public interface Challenge {
    void start(MinecraftServer server);
    void tick(MinecraftServer server);
    void preChallengeTick(MinecraftServer server);
}
