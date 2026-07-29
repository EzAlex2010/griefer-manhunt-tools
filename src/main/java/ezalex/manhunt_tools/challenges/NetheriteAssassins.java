package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.Timer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class NetheriteAssassins {
    public static Timer countdown = new Timer(Timer.Mode.COUNTDOWN, 144000);

    public static void start(MinecraftServer server) {
        countdown.start();
        give_items(server);
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
        // Curse of Binding
        Holder<Enchantment> binding = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.BINDING_CURSE);
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(binding, 1);

        stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());

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
                player.setItemSlot(EquipmentSlot.FEET, createArmor(Items.NETHERITE_BOOTS, server));
                player.setItemSlot(EquipmentSlot.LEGS, createArmor(Items.NETHERITE_LEGGINGS, server));
                player.setItemSlot(EquipmentSlot.CHEST, createArmor(Items.NETHERITE_CHESTPLATE, server));
                player.setItemSlot(EquipmentSlot.HEAD, createArmor(Items.NETHERITE_HELMET, server));
                // Sword
                player.getInventory().add(createSword());
            }
        }
    }
}
