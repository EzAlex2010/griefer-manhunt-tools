package ezalex.manhunt_tools;

import net.minecraft.network.RegistryFriendlyByteBuf;

public class Config {
    public String challenge = "classic"; //configured elsewhere
    public int compassUpdateInterval = 20;
    public boolean showTeamColors = true;
    public boolean giveHuntersCompass = true;
    public boolean hunterFriendlyFire = true;
    public boolean challengeRunning = false; // shouldn't even be in the config
    public boolean showTimer = true;
    public int timerLength = 144000;

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(challenge);
        buf.writeInt(compassUpdateInterval);
        buf.writeBoolean(showTeamColors);
        buf.writeBoolean(giveHuntersCompass);
        buf.writeBoolean(hunterFriendlyFire);
        buf.writeBoolean(showTimer);
        buf.writeInt(timerLength);
    }

    public static Config read(RegistryFriendlyByteBuf buf) {
        Config config = new Config();

        config.challenge = buf.readUtf();
        config.compassUpdateInterval = buf.readInt();
        config.showTeamColors = buf.readBoolean();
        config.giveHuntersCompass = buf.readBoolean();
        config.hunterFriendlyFire = buf.readBoolean();
        config.showTimer = buf.readBoolean();
        config.timerLength = buf.readInt();

        return config;
    }
}
