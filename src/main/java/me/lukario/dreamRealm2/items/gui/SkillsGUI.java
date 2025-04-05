package me.lukario.dreamRealm2.items.gui;

import me.lukario.dreamRealm2.Misc;
import me.lukario.dreamRealm2.items.special.ranged.bow.Freja;
import me.lukario.dreamRealm2.items.special.ranged.misc.GraveYard;
import me.lukario.dreamRealm2.items.swords.Rapier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SkillsGUI implements Listener {

    private final Plugin plugin;
    private final NamespacedKey strengthKey;
    private final NamespacedKey speedKey;
    private final NamespacedKey defenseKey;

    public SkillsGUI(Plugin plugin) {
        this.plugin = plugin;
        this.strengthKey = new NamespacedKey(plugin, "strength_level");
        this.speedKey = new NamespacedKey(plugin, "speed_level");
        this.defenseKey = new NamespacedKey(plugin, "defense_level");
    }

    public static void skillsGuiCreate(Player player){
        if (player==null){return;}
        UUID uuid = player.getUniqueId();

        Inventory inventory = Bukkit.createInventory(player,54,"Skills Upgrade");

        Misc.createInventoryItem(inventory, Material.IRON_SWORD,11,10302,"Strength","Click to upgrade strength");
        Misc.createInventoryItem(inventory,Material.RABBIT_FOOT,13,10303,"Speed","Click to upgrade speed");
        Misc.createInventoryItem(inventory,Material.SHIELD,15,10304,"Defense","Click to upgrade defense");

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClickInShop(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Skills Upgrade")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();

            if (item == null) return;
            ItemMeta meta = item.getItemMeta();

            if (meta.hasCustomModelData()) {
                int modelData = meta.getCustomModelData();
                if (modelData == 10302) {
                    int currentStrength = player.getPersistentDataContainer()
                        .getOrDefault(strengthKey, PersistentDataType.INTEGER, 0);

                    currentStrength++;

                    player.getPersistentDataContainer().set(strengthKey, PersistentDataType.INTEGER, currentStrength);

                    player.sendMessage("Strength upgraded to: " + currentStrength);
                }
                if (modelData == 10303) {
                    int currentSpeed = player.getPersistentDataContainer().getOrDefault(speedKey, PersistentDataType.INTEGER, 0);

                    currentSpeed++;

                    player.getPersistentDataContainer().set(speedKey, PersistentDataType.INTEGER, currentSpeed);

                    player.sendMessage("Speed upgraded to: " + currentSpeed);
                }
                if (modelData == 10304) {
                    int currentDefense = player.getPersistentDataContainer()
                        .getOrDefault(defenseKey, PersistentDataType.INTEGER, 0);

                    currentDefense++;

                    player.getPersistentDataContainer().set(defenseKey, PersistentDataType.INTEGER, currentDefense);

                    player.sendMessage("Defense upgraded to: " + currentDefense);
                }
            }
        }
    }
}
