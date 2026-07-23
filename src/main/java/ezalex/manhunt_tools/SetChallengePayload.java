package ezalex.manhunt_tools;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SetChallengePayload(String challenge) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetChallengePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(
                    GrieferManhuntTools.MOD_ID,
                    "set_challenge"
            ));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetChallengePayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    SetChallengePayload::challenge,
                    SetChallengePayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
