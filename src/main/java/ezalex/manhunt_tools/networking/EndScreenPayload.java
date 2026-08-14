package ezalex.manhunt_tools.networking;

import ezalex.manhunt_tools.GrieferManhuntTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EndScreenPayload(
        Component title,
        Component subtitle
) implements CustomPacketPayload {

    public static final Type<EndScreenPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    GrieferManhuntTools.MOD_ID,
                    "end_screen"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, EndScreenPayload> CODEC =
            StreamCodec.composite(
                    ComponentSerialization.STREAM_CODEC,
                    EndScreenPayload::title,
                    ComponentSerialization.STREAM_CODEC,
                    EndScreenPayload::subtitle,
                    EndScreenPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}