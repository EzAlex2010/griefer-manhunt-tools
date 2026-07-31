package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.Timer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
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

import java.util.Set;

public class NetheriteAssassins {
    public static Timer countdown;

    public static void start(MinecraftServer server) {
        countdown = new Timer(Timer.Mode.COUNTDOWN, ConfigManager.get().timerLength * 20);
        countdown.start();
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
        Holder<Enchantment> vanishing = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.VANISHING_CURSE);
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(binding, 1);
        enchants.set(vanishing, 1);

        stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());

        return stack;
    }

    private static ItemStack createSword(MinecraftServer server) {
        ItemStack stack = new ItemStack(Items.NETHERITE_SWORD);

        // Unbreakable
        stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);

        Holder<Enchantment> vanishing = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.VANISHING_CURSE);
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(vanishing, 1);
        stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());

        return stack;
    }

    public static void give_items(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
                // Armor
                if (player.getItemBySlot(EquipmentSlot.FEET).getItem() != Items.NETHERITE_BOOTS) player.setItemSlot(EquipmentSlot.FEET, createArmor(Items.NETHERITE_BOOTS, server));
                if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() != Items.NETHERITE_LEGGINGS) player.setItemSlot(EquipmentSlot.LEGS, createArmor(Items.NETHERITE_LEGGINGS, server));
                if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() != Items.NETHERITE_CHESTPLATE) player.setItemSlot(EquipmentSlot.CHEST, createArmor(Items.NETHERITE_CHESTPLATE, server));
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() != Items.NETHERITE_HELMET)player.setItemSlot(EquipmentSlot.HEAD, createArmor(Items.NETHERITE_HELMET, server));
                // Sword
                if (!player.getInventory().contains(createSword(server))) player.getInventory().add(createSword(server));
            }
        }
    }
}
