package ezalex.manhunt_tools;

import net.minecraft.ChatFormatting;
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

    private static int lerpColor(int start, int end, double t) {
        t = Math.clamp(t, 0.0, 1.0);

        int sr = (start >> 16) & 0xFF;
        int sg = (start >> 8) & 0xFF;
        int sb = start & 0xFF;

        int er = (end >> 16) & 0xFF;
        int eg = (end >> 8) & 0xFF;
        int eb = end & 0xFF;

        int r = (int) (sr + (er - sr) * t);
        int g = (int) (sg + (eg - sg) * t);
        int b = (int) (sb + (eb - sb) * t);

        return (r << 16) | (g << 8) | b;
    }

    public Component getFormattedComponent() {
        int color; // white

        if (mode == Mode.COUNTDOWN) {
            double percent = (double) ticks / initialTicks;
            int seconds = getSeconds();

            if (seconds <= 10) {
                color = (ticks / 10) % 2 == 0
                        ? 0xFF5555
                        : 0xAA0000;
            } else if (seconds <= 60) {
                color = 0xFFA500;
            } else {
                // Progress from start -> 1 minute remaining
                double t = 1.0 - ((double)(ticks - 1200) / (initialTicks - 1200));
                color = lerpColor(0x55FF55, 0xFFA500, t);
            }
        } else {
            color = 0xFFFFFF;
        }

        return Component.literal(getFormattedTime()).withStyle(style -> style.withColor(color));
    }

    public void showTo(ServerPlayer player) {
        player.connection.send(
            new ClientboundSetActionBarTextPacket(
                getFormattedComponent()
            )
        );
    }
}
