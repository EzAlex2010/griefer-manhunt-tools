package ezalex.manhunt_tools.networking;

import ezalex.manhunt_tools.GrieferManhuntTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestConfigPayload() implements CustomPacketPayload {

    public static final Type<RequestConfigPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    GrieferManhuntTools.MOD_ID,
                    "request_config"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestConfigPayload> CODEC =
            CustomPacketPayload.codec(
                    (payload, buf) -> {},
                    buf -> new RequestConfigPayload()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
