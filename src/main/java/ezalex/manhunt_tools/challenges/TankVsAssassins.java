package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.Manager;
import ezalex.manhunt_tools.TeamManager;
import ezalex.manhunt_tools.Timer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
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

public class TankVsAssassins implements Challenge {
    @Override
    public void start(MinecraftServer server) {
        Manager.getTimer().configureCountdown((ConfigManager.get().timerLength * 20L));
    }
    @Override
    public void tick(MinecraftServer server) {}
    @Override
    public void preChallengeTick(MinecraftServer server) {
        give_items(server);
    }

    private ItemStack createArmor(Item item, MinecraftServer server, boolean runner) {
        ItemStack stack = new ItemStack(item);

        // Tracking Data
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("from_griefer_manhunt_tools", true);
        // Unbreakable
        if (!runner) stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        // Curse of Binding
        Holder<Enchantment> vanishing = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.VANISHING_CURSE);
        Holder<Enchantment> protection = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.PROTECTION);
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(vanishing, 1);
        if (runner) enchants.set(protection, 4);

        stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());

        return stack;
    }

    private ItemStack createHunterSword(MinecraftServer server) {
        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);

        // Tracking Data
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("from_griefer_manhunt_tools", true);
        // Unbreakable
        stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);

        Holder<Enchantment> vanishing = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.VANISHING_CURSE);
        Holder<Enchantment> sharpness = server.registryAccess()
                .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SHARPNESS);
        ItemEnchantments.Mutable enchants = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchants.set(vanishing, 1);
        enchants.set(sharpness, 5);
        stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());

        return stack;
    }

    public void give_items(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (TeamManager.isHunter(player)) {
                // Armor
                if (player.getItemBySlot(EquipmentSlot.FEET).getItem() != Items.CHAINMAIL_BOOTS) player.setItemSlot(EquipmentSlot.FEET, createArmor(Items.CHAINMAIL_BOOTS, server, false));
                if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() != Items.CHAINMAIL_LEGGINGS) player.setItemSlot(EquipmentSlot.LEGS, createArmor(Items.CHAINMAIL_LEGGINGS, server, false));
                if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() != Items.CHAINMAIL_CHESTPLATE) player.setItemSlot(EquipmentSlot.CHEST, createArmor(Items.CHAINMAIL_CHESTPLATE, server, false));
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() != Items.CHAINMAIL_HELMET)player.setItemSlot(EquipmentSlot.HEAD, createArmor(Items.CHAINMAIL_HELMET, server, false));
                // Sword
                if (!player.getInventory().contains(createHunterSword(server))) player.getInventory().add(createHunterSword(server));
            }
            if (TeamManager.isRunner(player)) {
                // Armor
                if (player.getItemBySlot(EquipmentSlot.FEET).getItem() != Items.DIAMOND_BOOTS) player.setItemSlot(EquipmentSlot.FEET, createArmor(Items.DIAMOND_BOOTS, server, true));
                if (player.getItemBySlot(EquipmentSlot.LEGS).getItem() != Items.DIAMOND_LEGGINGS) player.setItemSlot(EquipmentSlot.LEGS, createArmor(Items.DIAMOND_LEGGINGS, server, true));
                if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() != Items.DIAMOND_CHESTPLATE) player.setItemSlot(EquipmentSlot.CHEST, createArmor(Items.DIAMOND_CHESTPLATE, server, true));
                if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() != Items.DIAMOND_HELMET)player.setItemSlot(EquipmentSlot.HEAD, createArmor(Items.DIAMOND_HELMET, server, true));
                // Sword
                if (!player.getInventory().contains(new ItemStack(Items.WOODEN_SWORD))) player.getInventory().add(new ItemStack(Items.WOODEN_SWORD));
            }
        }
    }
}
