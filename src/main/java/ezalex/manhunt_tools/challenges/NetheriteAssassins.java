package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.GrieferManhuntTools;
import ezalex.manhunt_tools.Timer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class NetheriteAssassins {
    public static Timer countdown = new Timer(Timer.Mode.COUNTDOWN, 144000, true);

    public static void start(MinecraftServer server) {
        countdown.start();
        GrieferManhuntTools.LOGGER.info("giving items");
        give_items(server);
        GrieferManhuntTools.LOGGER.info("ran start");
    }

    public static void tick(MinecraftServer server) {
        countdown.tick();
        if (ConfigManager.get().showTimer) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                countdown.showTo(player);
            }
        }
    }

    private static ItemStack createArmor(Item item, MinecraftServer server) {
        ItemStack stack = new ItemStack(item);

        // Unbreakable
        stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        GrieferManhuntTools.LOGGER.info("1");
        // Curse of Binding
        Holder<Enchantment> binding = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.BINDING_CURSE);
        GrieferManhuntTools.LOGGER.info("2");
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(binding, 1);
        GrieferManhuntTools.LOGGER.info("3");

        stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());
        GrieferManhuntTools.LOGGER.info("4");

        return stack;
    }

    private static ItemStack createSword() {
        ItemStack stack = new ItemStack(Items.NETHERITE_SWORD);

        // Unbreakable
        stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);

        return stack;
    }

    public static void give_items(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
                // Armor
                //player.getInventory().getSlot(103).set(createArmor(Items.NETHERITE_HELMET, server));
                //player.getInventory().getSlot(102).set(createArmor(Items.NETHERITE_CHESTPLATE, server));
                //player.getInventory().getSlot(101).set(createArmor(Items.NETHERITE_LEGGINGS, server));
                player.getInventory().getSlot(100).set(createArmor(Items.NETHERITE_BOOTS, server));

                // Sword
                player.getInventory().add(createSword());
            }
        }
    }
}
