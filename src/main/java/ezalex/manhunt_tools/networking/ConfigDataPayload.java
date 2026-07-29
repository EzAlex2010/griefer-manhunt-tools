package ezalex.manhunt_tools.networking;

import ezalex.manhunt_tools.Config;
import ezalex.manhunt_tools.GrieferManhuntTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ConfigDataPayload(Config config) implements CustomPacketPayload {
    public static final Type<ConfigDataPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    GrieferManhuntTools.MOD_ID,
                    "config_data"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigDataPayload> CODEC =
            CustomPacketPayload.codec(
                    (payload, buf) -> payload.config().write(buf),
                    buf -> new ConfigDataPayload(Config.read(buf))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
