package ezalex.manhunt_tools;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class Timer {

    public enum Mode {
        COUNTDOWN,
        STOPWATCH
    }

    private final Mode mode;

    private int ticks;
    private final int initialTicks;

    private boolean running;
    private boolean paused;

    public Timer(Mode mode, int startingTicks) {
        this.mode = mode;
        this.initialTicks = startingTicks;
        this.ticks = startingTicks;
    }

    public void tick() {
        if (!running || paused) {
            return;
        }

        switch (mode) {
            case COUNTDOWN -> {
                if (ticks > 0) {
                    ticks--;
                }
            }

            case STOPWATCH -> ticks++;
        }
    }

    public void start() {
        running = true;
        paused = false;
    }

    public void stop() {
        running = false;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void reset() {
        ticks = (mode == Mode.COUNTDOWN) ? initialTicks : 0;
    }

    public boolean isFinished() {
        return mode == Mode.COUNTDOWN && ticks <= 0;
    }

    public int getTicks() {
        return ticks;
    }

    public int getSeconds() {
        return ticks / 20;
    }

    public boolean isRunning() {
        return running;
    }

    public String getFormattedTime() {
        int totalSeconds = ticks / 20;

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }

        return String.format("%02d:%02d", minutes, seconds);
    }

    public void showTo(ServerPlayer player) {
        player.connection.send(
            new ClientboundSetActionBarTextPacket(
                Component.literal(getFormattedTime())
            )
        );
    }
}
