package ezalex.manhunt_tools;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenChallengeScreenPayload() implements CustomPacketPayload {

    public static final Type<OpenChallengeScreenPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    GrieferManhuntTools.MOD_ID,
                    "open_challenge_screen"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenChallengeScreenPayload> CODEC =
            CustomPacketPayload.codec(
                    (payload, buf) -> {},
                    buf -> new OpenChallengeScreenPayload()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
