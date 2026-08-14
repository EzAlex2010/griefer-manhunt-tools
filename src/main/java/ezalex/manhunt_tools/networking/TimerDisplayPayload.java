package ezalex.manhunt_tools.networking;

import ezalex.manhunt_tools.GrieferManhuntTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record TimerDisplayPayload(
        Component text
) implements CustomPacketPayload {

    public static final Type<TimerDisplayPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    GrieferManhuntTools.MOD_ID,
                    "timer_display"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, TimerDisplayPayload> CODEC =
            StreamCodec.composite(
                    ComponentSerialization.STREAM_CODEC,
                    TimerDisplayPayload::text,
                    TimerDisplayPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}