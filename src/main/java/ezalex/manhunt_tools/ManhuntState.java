package ezalex.manhunt_tools;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;


public class ManhuntState extends SavedData {
    public boolean challengeRunning = false;
    public String currentChallenge;
    public Timer.Mode timerMode;
    public int timerTicks;
    public int timerInitialTicks;

    public ManhuntState(boolean challengeRunning, String currentChallenge, String timerMode, int timerTicks, int timerInitialTicks) {
        this.challengeRunning = challengeRunning;
        this.currentChallenge = currentChallenge;
        this.timerMode = Timer.Mode.valueOf(timerMode);
        this.timerTicks = timerTicks;
        this.timerInitialTicks = timerInitialTicks;
    }

    public ManhuntState() {
    }

    private static final Codec<ManhuntState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("challengeRunning").forGetter(s -> s.challengeRunning),
                    Codec.STRING.fieldOf("currentChallenge").forGetter(s -> s.currentChallenge),
                    Codec.STRING.fieldOf("timerMode").forGetter(s -> s.timerMode.toString()),
                    Codec.INT.fieldOf("timerTicks").forGetter(s -> s.timerTicks),
                    Codec.INT.fieldOf("timerInitialTicks").forGetter(s -> s.timerInitialTicks)
            ).apply(instance, ManhuntState::new)
    );

    private static final SavedDataType<ManhuntState> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(
                            GrieferManhuntTools.MOD_ID,
                            "manhunt_state"
                    ),
                    ManhuntState::new,
                    CODEC,
                    null
            );

    public static ManhuntState get(MinecraftServer server) {
        ServerLevel level = server.overworld();
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void copyFromManager() {
        challengeRunning = Manager.challengeRunning;

        Timer timer = Manager.getTimer();

        timerMode = timer.getMode();
        timerTicks = timer.getTicks();
        timerInitialTicks = timer.getInitialTicks();

        setDirty();
    }

    public void applyToManager() {
        Manager.challengeRunning = challengeRunning;
        Timer timer = Manager.getTimer();
        Timer.Mode mode = timerMode;
        if (mode == Timer.Mode.COUNTDOWN) {
            timer.configureCountdown(timerInitialTicks);
        } else {
            timer.configureStopwatch();
        }
        timer.setTicks(timerTicks);
    }
}
