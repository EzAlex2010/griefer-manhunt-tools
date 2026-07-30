package ezalex.manhunt_tools;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class ManhuntState extends SavedData {
    public boolean challengeRunning = false;

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("challengeRunning", challengeRunning);
        return tag;
    }

    public static ManhuntState load(CompoundTag tag, HolderLookup.Provider registries) {
        ManhuntState state = new ManhuntState();
        state.challengeRunning = tag.getBoolean("challengeRunning").orElse(false);
        return state;
    }
}
