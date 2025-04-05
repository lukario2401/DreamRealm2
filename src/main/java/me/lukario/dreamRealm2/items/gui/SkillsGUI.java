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
    private static NamespacedKey strengthKey = null;
    private static NamespacedKey speedKey = null;
    private static NamespacedKey defenseKey = null;
    private static NamespacedKey pointsKey;
    private static NamespacedKey boughtPointsKey;


    public SkillsGUI(Plugin plugin) {
        this.plugin = plugin;
        this.strengthKey = new NamespacedKey(plugin, "strength_level");
        this.speedKey = new NamespacedKey(plugin, "speed_level");
        this.defenseKey = new NamespacedKey(plugin, "defense_level");
        this.pointsKey = new NamespacedKey(plugin, "points");
        this.boughtPointsKey = new NamespacedKey(plugin, "bought_points");
    }

     public static int getStrength(Player player) {
        return player.getPersistentDataContainer()
            .getOrDefault(strengthKey, PersistentDataType.INTEGER, 0);
    }

    public static void setStrength(Player player, int value) {
        player.getPersistentDataContainer()
            .set(strengthKey, PersistentDataType.INTEGER, value);
    }

    public static int getPoints(Player player) {
        return player.getPersistentDataContainer()
            .getOrDefault(pointsKey, PersistentDataType.INTEGER, 0);
    }
    public static void setPoints(Player player, int value) {
        player.getPersistentDataContainer()
            .set(pointsKey, PersistentDataType.INTEGER, value);
    }

    public static int getDefense(Player player) {
        return player.getPersistentDataContainer()
            .getOrDefault(defenseKey, PersistentDataType.INTEGER, 0);
    }
    public static void setDefense(Player player, int value) {
        player.getPersistentDataContainer()
            .set(defenseKey, PersistentDataType.INTEGER, value);
    }
    public static int getSpeed(Player player) {
        return player.getPersistentDataContainer()
            .getOrDefault(speedKey, PersistentDataType.INTEGER, 0);
    }
    public static void setSpeed(Player player, int value) {
        player.getPersistentDataContainer()
            .set(speedKey, PersistentDataType.INTEGER, value);
    }
    public static int getPointsBought(Player player) {
        return player.getPersistentDataContainer()
            .getOrDefault(boughtPointsKey, PersistentDataType.INTEGER, 0);
    }
    public static void setPointsBought(Player player, int value) {
        player.getPersistentDataContainer()
            .set(boughtPointsKey, PersistentDataType.INTEGER, value);
    }

    public static void skillsGuiCreate(Player player){
        if (player==null){return;}
        UUID uuid = player.getUniqueId();

        Inventory inventory = Bukkit.createInventory(player,54,"Skills Upgrade");

        Misc.createInventoryItem(inventory, Material.IRON_SWORD,11,10302,"Strength","Click to upgrade strength","Level: "+getStrength(player));
        Misc.createInventoryItem(inventory,Material.RABBIT_FOOT,13,10303,"Speed","Click to upgrade speed","Level: "+getSpeed(player));
        Misc.createInventoryItem(inventory,Material.SHIELD,15,10304,"Defense","Click to upgrade defense","Level: "+getDefense(player));

        Misc.createInventoryItem(inventory,Material.BARRIER,22,10305,"Reset","You have: "+getPoints(player)+" points");
        Misc.createInventoryItem(inventory,Material.ARROW,45,10306,"Back","Click to go back");

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClickInShop(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Skills Upgrade")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            Inventory inventory = event.getInventory();

            if (item == null) return;
            ItemMeta meta = item.getItemMeta();

            if (meta.hasCustomModelData()) {

                int points = player.getPersistentDataContainer().getOrDefault(pointsKey,PersistentDataType.INTEGER,0);
                int modelData = meta.getCustomModelData();

                if (modelData == 10305) {

                        player.getPersistentDataContainer().set(strengthKey, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(speedKey, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(defenseKey, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(boughtPointsKey, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(pointsKey, PersistentDataType.INTEGER, 0);
                        return;
                }
                if (modelData == 10306) {

                    player.closeInventory();
                    GUIItem.openMainGUI(player);
                    return;
                }

                if(points>0){
                    if (modelData == 10302) {
                        int currentStrength = player.getPersistentDataContainer()
                            .getOrDefault(strengthKey, PersistentDataType.INTEGER, 0);

                        currentStrength++;

                        player.getPersistentDataContainer().set(strengthKey, PersistentDataType.INTEGER, currentStrength);
                        player.getPersistentDataContainer().set(pointsKey, PersistentDataType.INTEGER, points-1);

                        Misc.createInventoryItem(inventory, Material.IRON_SWORD,11,10302,"Strength","Click to upgrade strength","Level: "+getStrength(player));
                        player.sendMessage("Strength upgraded to: " + currentStrength);
                    }
                    if (modelData == 10303) {
                        int currentSpeed = player.getPersistentDataContainer().getOrDefault(speedKey, PersistentDataType.INTEGER, 0);

                        currentSpeed++;

                        player.getPersistentDataContainer().set(speedKey, PersistentDataType.INTEGER, currentSpeed);
                        player.getPersistentDataContainer().set(pointsKey, PersistentDataType.INTEGER, points-1);

                        Misc.createInventoryItem(inventory,Material.RABBIT_FOOT,13,10303,"Speed","Click to upgrade speed","Level: "+getSpeed(player));
                        player.sendMessage("Speed upgraded to: " + currentSpeed);
                    }
                    if (modelData == 10304) {
                        int currentDefense = player.getPersistentDataContainer()
                            .getOrDefault(defenseKey, PersistentDataType.INTEGER, 0);

                        currentDefense++;

                        player.getPersistentDataContainer().set(defenseKey, PersistentDataType.INTEGER, currentDefense);
                        player.getPersistentDataContainer().set(pointsKey, PersistentDataType.INTEGER, points-1);

                        Misc.createInventoryItem(inventory,Material.SHIELD,15,10304,"Defense","Click to upgrade defense","Level: "+getDefense(player));
                        player.sendMessage("Defense upgraded to: " + currentDefense);
                    }
                }else{
                    player.sendMessage("You don't have enough points");
                }
            }
        }
    }
}
