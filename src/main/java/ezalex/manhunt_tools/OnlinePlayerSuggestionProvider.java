package ezalex.manhunt_tools;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class OnlinePlayerSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<CommandSourceStack> context,
            SuggestionsBuilder builder) {

        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            builder.suggest(player.getGameProfile().name());
        }

        return builder.buildFuture();
    }
}