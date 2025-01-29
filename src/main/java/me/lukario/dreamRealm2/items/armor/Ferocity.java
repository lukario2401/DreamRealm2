package me.lukario.dreamRealm2.items.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Ferocity implements Listener {

    public static HashMap<UUID, Integer> playerFerocity = new HashMap<>();

    private static final String ITEM_NAME = ChatColor.of("#e668c6") + "Netherite Helmet";
    // Fixed lore to use consistent color coding
    private static final String ITEM_LORE = ChatColor.of("#FFD700") + "Ferocious Helmet";
    private static final Material ITEM_MATERIAL = Material.NETHERITE_HELMET;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Collections.singletonList(ITEM_LORE));
            meta.addEnchant(Enchantment.PROTECTION,4,false);
            meta.addEnchant(Enchantment.THORNS,3,false);
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static final String ITEM_NAME_CHESTPLATE = ChatColor.of("#e668c6") + "Netherite Chestplate";
    // Fixed lore to use consistent color coding
    private static final String ITEM_LORE_CHESTPLATE = ChatColor.of("#FFD700") + "Ferocious Elytra";
    private static final Material ITEM_MATERIAL_CHESTPLATE = Material.ELYTRA;

    public static ItemStack createItemChestplate() {
        ItemStack item = new ItemStack(ITEM_MATERIAL_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME_CHESTPLATE);
            meta.setLore(Collections.singletonList(ITEM_LORE_CHESTPLATE));
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.PROTECTION,4,false);
            meta.addEnchant(Enchantment.THORNS,3,false);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static final String ITEM_NAME_LEGGINGS = ChatColor.of("#e668c6") + "Netherite Leggings";
    // Fixed lore to use consistent color coding
    private static final String ITEM_LORE_LEGGINGS = ChatColor.of("#FFD700") + "Ferocious Leggings";
    private static final Material ITEM_MATERIAL_LEGGINGS = Material.NETHERITE_LEGGINGS;

    public static ItemStack createItemLeggings() {
        ItemStack item = new ItemStack(ITEM_MATERIAL_LEGGINGS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME_LEGGINGS);
            meta.setLore(Collections.singletonList(ITEM_LORE_LEGGINGS));
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.PROTECTION,4,false);
            meta.addEnchant(Enchantment.THORNS,3,false);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static final String ITEM_NAME_BOOTS = ChatColor.of("#e668c6") + "Netherite boots";
    // Fixed lore to use consistent color coding
    private static final String ITEM_LORE_BOOTS = ChatColor.of("#FFD700") + "Ferocious Boots";
    private static final Material ITEM_MATERIAL_BOOTS = Material.NETHERITE_BOOTS;

    public static ItemStack createItemBoots() {
        ItemStack item = new ItemStack(ITEM_MATERIAL_BOOTS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME_BOOTS);
            meta.setLore(Collections.singletonList(ITEM_LORE_BOOTS));
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.PROTECTION,4,false);
            meta.addEnchant(Enchantment.THORNS,3,false);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onEquipmentChange(PlayerArmorChangeEvent event) {
        ItemStack oldItem = event.getOldItem();
        ItemStack newItem = event.getNewItem();
        UUID playerId = event.getPlayer().getUniqueId();

        // Handle old item removal first
        if (isFerocityItem(oldItem)) {
            adjustFerocity(playerId, -50);
        }

        // Then handle new item addition
        if (isFerocityItem(newItem)) {
            adjustFerocity(playerId, 50);
        }
    }

    private boolean isFerocityItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        // Check for "ferocious" in any color/case combination
        return meta.getLore().stream()
                .anyMatch(line -> ChatColor.stripColor(line).toLowerCase().contains("ferocious"));
    }


    private void adjustFerocity(UUID playerId, int amount) {
        int current = playerFerocity.getOrDefault(playerId, 0);
        playerFerocity.put(playerId, Math.max(0, current + amount));
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // Proper damage event handling
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            int ferocity = getFerocity(player.getUniqueId());

            // Apply ferocity bonus (example: 1% damage per ferocity point)
            double bonusDamage = event.getDamage() * (ferocity / 100.0);
            event.setDamage(event.getDamage() + bonusDamage);

            // Optional feedback message
            player.sendMessage(ChatColor.GOLD + "Ferocity bonus: +" + bonusDamage + " damage!");
        }
    }

    public static int getFerocity(UUID playerId) {
        return playerFerocity.getOrDefault(playerId, 0);
    }
}