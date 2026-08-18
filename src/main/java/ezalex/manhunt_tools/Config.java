package ezalex.manhunt_tools;

import net.minecraft.network.RegistryFriendlyByteBuf;

public class Config {
    public String challenge = "classic";
    public int compassUpdateInterval = 20;
    public boolean showTeamColors = true;
    public boolean giveHuntersCompass = true;
    public boolean hunterFriendlyFire = true;
    public Long timerLength = 7200L;
    public boolean keepInventory = false;
    public int worldBoarderSize = 999999999;

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(challenge);
        buf.writeInt(compassUpdateInterval);
        buf.writeBoolean(showTeamColors);
        buf.writeBoolean(giveHuntersCompass);
        buf.writeBoolean(hunterFriendlyFire);
        buf.writeLong(timerLength);
        buf.writeBoolean(keepInventory);
        buf.writeInt(worldBoarderSize);
    }

    public static Config read(RegistryFriendlyByteBuf buf) {
        Config config = new Config();

        config.challenge = buf.readUtf();
        config.compassUpdateInterval = buf.readInt();
        config.showTeamColors = buf.readBoolean();
        config.giveHuntersCompass = buf.readBoolean();
        config.hunterFriendlyFire = buf.readBoolean();
        config.timerLength = buf.readLong();
        config.keepInventory = buf.readBoolean();
        config.worldBoarderSize = buf.readInt();

        return config;
    }
}
