package me.lukario.dreamRealm2.items.gui;

import me.lukario.dreamRealm2.Misc;
import me.lukario.dreamRealm2.items.special.ranged.bow.Freja;
import me.lukario.dreamRealm2.items.special.ranged.misc.GraveYard;
import me.lukario.dreamRealm2.items.swords.Rapier;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.attribute.Attribute;

import java.util.UUID;

public class SkillsGUI implements Listener {

    private final Plugin plugin;
    private static NamespacedKey strengthKey = null;
    private static NamespacedKey speedKey = null;
    private static NamespacedKey defenseKey = null;
    private static NamespacedKey reachKey = null;
    private static NamespacedKey attackspeedKey = null;
    private static NamespacedKey healthKey = null;
    private static NamespacedKey pointsKey;
    private static NamespacedKey boughtPointsKey;


    public SkillsGUI(Plugin plugin) {
        this.plugin = plugin;
        this.strengthKey = new NamespacedKey(plugin, "strength_level");
        this.speedKey = new NamespacedKey(plugin, "speed_level");
        this.defenseKey = new NamespacedKey(plugin, "defense_level");
        this.reachKey = new NamespacedKey(plugin, "reach_key");
        this.attackspeedKey = new NamespacedKey(plugin, "attack_speed_key");
        this.healthKey = new NamespacedKey(plugin, "attack_speed_key");
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

     public static int getHealth(Player player) {
        return player.getPersistentDataContainer()
            .getOrDefault(healthKey, PersistentDataType.INTEGER, 0);
    }

    public static void setHealth(Player player, int value) {
        player.getPersistentDataContainer()
            .set(healthKey, PersistentDataType.INTEGER, value);
    }

     public static int getAttackSpeed(Player player) {
        return player.getPersistentDataContainer()
            .getOrDefault(attackspeedKey, PersistentDataType.INTEGER, 0);
    }

    public static void setAttackSpeed(Player player, int value) {
        player.getPersistentDataContainer()
            .set(attackspeedKey, PersistentDataType.INTEGER, value);
    }

     public static int getReach(Player player) {
        return player.getPersistentDataContainer()
            .getOrDefault(reachKey, PersistentDataType.INTEGER, 0);
    }

    public static void setReach(Player player, int value) {
        player.getPersistentDataContainer()
            .set(reachKey, PersistentDataType.INTEGER, value);
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

        Misc.createInventoryItem(inventory, Material.WOODEN_SWORD,11,10302,"Strength","Click to upgrade strength","Level: "+getStrength(player));
        Misc.createInventoryItem(inventory,Material.RABBIT_FOOT,13,10303,"Speed","Click to upgrade speed","Level: "+getSpeed(player));
        Misc.createInventoryItem(inventory,Material.SHIELD,15,10304,"Defense","Click to upgrade defense","Level: "+getDefense(player));

        Misc.createInventoryItem(inventory, Material.STONE_SWORD,20,10307,"Reach","Click to upgrade reach","Level: "+getReach(player));
        Misc.createInventoryItem(inventory, Material.APPLE,24,10309,"Health","Click to upgrade Health","Level: "+getHealth(player));

        Misc.createInventoryItem(inventory, Material.IRON_SWORD,29,10308,"Attack Speed","Click to upgrade Attack Speed","Level: "+getAttackSpeed(player));

        Misc.createInventoryItem(inventory,Material.BARRIER,49,10305,"Reset","You have: "+getPoints(player)+" points");
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
                        player.getPersistentDataContainer().set(reachKey, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(attackspeedKey, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(healthKey, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(boughtPointsKey, PersistentDataType.INTEGER, 0);
                        player.getPersistentDataContainer().set(pointsKey, PersistentDataType.INTEGER, 0);

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute "+player.getName()+" minecraft:movement_speed modifier remove 0000001");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute "+player.getName()+" minecraft:armor modifier remove 0000001");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute "+player.getName()+" minecraft:entity_interaction_range modifier remove 0000001");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute "+player.getName()+" minecraft:block_interaction_range modifier remove 0000001");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + player.getName() + " minecraft:attack_speed modifier remove 0000001");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + player.getName() + " minecraft:max_health modifier remove 0000001");

                        player.closeInventory();

                        return;
                }
                if (modelData == 10306) {

                    player.closeInventory();
                    GUIItem.openMainGUI(player);
                    return;
                }

                if (points > 0) {
                    // Determine how many upgrades to apply: 5 for shift-click, 1 otherwise,
                    // but not more than the available points.
                    int upgrades = event.isShiftClick() ? Math.min(5, points) : 1;

                    if (modelData == 10302) { // Strength upgrade
                        int currentStrength = getStrength(player);
                        currentStrength += upgrades;
                        setStrength(player, currentStrength);
                        setPoints(player, points - upgrades);

                        // Update the inventory item with the new strength level.
                        Misc.createInventoryItem(inventory, Material.IRON_SWORD, 11, 10302,
                                                   "Strength", "Click to upgrade strength",
                                                   "Level: " + currentStrength);
                        player.sendMessage("Strength upgraded to: " + currentStrength);
                    }
                    if (modelData == 10303) { // Speed upgrade
                        int currentSpeed = getSpeed(player);
                        currentSpeed += upgrades;
                        setSpeed(player, currentSpeed);
                        setPoints(player, points - upgrades);

                        String playerName = player.getName();
                        // Remove the current speed modifier.
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:movement_speed modifier remove 0000001");
                        // Calculate new speed attribute; adjust formula as needed.
                        double speedAtt = (double) currentSpeed / 40;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:movement_speed modifier add 0000001 " + speedAtt + " add_value");

                        Misc.createInventoryItem(inventory, Material.RABBIT_FOOT, 13, 10303,
                                                   "Speed", "Click to upgrade speed",
                                                   "Level: " + currentSpeed);
                        player.sendMessage("Speed upgraded to: " + currentSpeed);
                    }
                    if (modelData == 10304) { // Defense upgrade
                        int currentDefense = getDefense(player);
                        currentDefense += upgrades;
                        setDefense(player, currentDefense);
                        setPoints(player, points - upgrades);

                        String playerName = player.getName();
                        // Remove the current defense modifier.
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:armor modifier remove 0000001");
                        double playerDefense = (double) currentDefense / 2;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:armor modifier add 0000001 " + playerDefense + " add_value");

                        Misc.createInventoryItem(inventory, Material.SHIELD, 15, 10304,
                                                   "Defense", "Click to upgrade defense",
                                                   "Level: " + currentDefense);
                        player.sendMessage("Defense upgraded to: " + currentDefense);
                    }
                     if (modelData == 10307) {
                        int currentReach = getReach(player);
                        currentReach += upgrades;
                        setReach(player, currentReach);
                        setPoints(player, points - upgrades);

                        String playerName = player.getName();

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:block_interaction_range modifier remove 0000001");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:entity_interaction_range modifier remove 0000001");
                        double playerReach = (double) currentReach / 10;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:block_interaction_range modifier add 0000001 " + playerReach + " add_value");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:entity_interaction_range modifier add 0000001 " + playerReach + " add_value");

                        skillsGuiCreate(player);

                        player.sendMessage("Reach upgraded to: " + playerReach);
                    }
                     if (modelData == 10308) {
                        int currentAttackSpeed = getAttackSpeed(player);
                        currentAttackSpeed += upgrades;
                        setAttackSpeed(player, currentAttackSpeed);
                        setPoints(player, points - upgrades);

                        String playerName = player.getName();

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:attack_speed modifier remove 0000001");
                        double playerAttackSpeed = (double) currentAttackSpeed / 20;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:attack_speed modifier add 0000001 " + playerAttackSpeed + " add_value");


                        skillsGuiCreate(player);

                        player.sendMessage("Reach upgraded to: " + playerAttackSpeed);
                    }
                     if (modelData == 10309) {
                        int currentHealth = getAttackSpeed(player);
                        currentHealth += upgrades;
                        setHealth(player, currentHealth);
                        setPoints(player, points - upgrades);

                        String playerName = player.getName();

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:max_health modifier remove 0000001");
                        double playerHealth = (double) currentHealth / 2;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "attribute " + playerName + " minecraft:max_health modifier add 0000001 " + playerHealth + " add_value");

                        skillsGuiCreate(player);

                        player.sendMessage("Reach upgraded to: " + playerHealth);
                    }
                } else {
                    player.sendMessage("You don't have enough points");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            double bonusDamage = (event.getDamage() * (getStrength(player) * 10))/100;
            event.setDamage(event.getDamage() + bonusDamage);

            player.sendMessage(ChatColor.GOLD + "Strength bonus: +" + bonusDamage + " damage!");
        }
    }
}
