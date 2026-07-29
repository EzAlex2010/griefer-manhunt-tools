package ezalex.manhunt_tools.networking;

import ezalex.manhunt_tools.Config;
import ezalex.manhunt_tools.GrieferManhuntTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SaveConfigPayload(Config config) implements CustomPacketPayload {
    public static final Type<SaveConfigPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    GrieferManhuntTools.MOD_ID,
                    "save_config"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveConfigPayload> CODEC =
            CustomPacketPayload.codec(
                    (payload, buf) -> payload.config().write(buf),
                    buf -> new SaveConfigPayload(Config.read(buf))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
