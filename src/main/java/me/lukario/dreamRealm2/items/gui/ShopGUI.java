package me.lukario.dreamRealm2.items.gui;

import me.lukario.dreamRealm2.Misc;
import me.lukario.dreamRealm2.items.special.ranged.bow.Freja;
import me.lukario.dreamRealm2.items.special.ranged.misc.GraveYard;
import me.lukario.dreamRealm2.items.swords.Rapier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class ShopGUI implements Listener {

    private static HashMap<UUID,Double> playersCash = new HashMap<>();
    private final JavaPlugin plugin;
    private final File dataFile;
    private final YamlConfiguration config;

    public ShopGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "player_cash.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        loadData();

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::saveData, 20L * 60 * 5, 20L * 60 * 5);
    }

    private void loadData() {
        if (!dataFile.exists()) return;

        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            double cash = config.getDouble(key);
            playersCash.put(uuid, cash);
        }
    }

    public void saveData() {

        Set<UUID> keySetKeyOh = playersCash.keySet();
        for (UUID uuid : keySetKeyOh){
            if (!playersCash.containsKey(uuid)){
                playersCash.put(uuid,0D);
            }
        }

        try {
            for (String key : config.getKeys(false)) {
                config.set(key, null);
            }
            for (Map.Entry<UUID, Double> entry : playersCash.entrySet()) {
                config.set(entry.getKey().toString(), entry.getValue());
            }
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player cash data: " + e.getMessage());
        }
    }

    public static double getPlayerCash(UUID uuid) {
        return playersCash.getOrDefault(uuid, 0D);
    }

    public void setPlayerCash(UUID uuid, double amount) {
        playersCash.put(uuid, amount);
    }

    public static void shopGuiCreate(Player player){
        if (player==null){return;}
        UUID uuid = player.getUniqueId();

        Inventory inventory = Bukkit.createInventory(player,54,"Shop");

        Misc.createInventoryItem(inventory,Material.CROSSBOW,11,10202,"Freja","Click to buy item Freja");
        Misc.createInventoryItem(inventory,Material.DIAMOND_SWORD,13,10203,"Rapier","Click to buy item Rapier");
        Misc.createInventoryItem(inventory,Material.IRON_SHOVEL,15,10204,"GraveYard","Click to buy item GraveYard");
        Misc.createInventoryItem(inventory, Material.EXPERIENCE_BOTTLE, 22, 10205, "Buy Points","Cost: "+SkillsGUI.getPointsBought(player)*100);

        String sunFlowerName = "You have: " + getPlayerCash(uuid) + " Coins";
        Misc.createInventoryItem(inventory, Material.SUNFLOWER, 49, 10210, sunFlowerName);


        if (!playersCash.containsKey(uuid)) {
            playersCash.put(uuid, 0D);
        }
        player.openInventory(inventory);
    }

    @EventHandler
public void onInventoryClickInShop(InventoryClickEvent event) {
    if (event.getView().getTitle().equals("Shop")) {
        event.setCancelled(true); // Prevent taking items

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        UUID uuid = player.getUniqueId();

        if (item == null) return;

        ItemMeta meta = item.getItemMeta();

        if (meta.hasCustomModelData()){
            if (item.getItemMeta().getCustomModelData() == 10202) {
                if (playersCash.get(uuid) >= 1200) {
                    playersCash.put(uuid, playersCash.get(uuid) - 1200);
                    player.getInventory().addItem(Freja.createItem());
                    Misc.createInventoryItem(event.getInventory(), Material.SUNFLOWER, 49, 10210, "You have: " + playersCash.get(player.getUniqueId()).toString() + " Coins");
                    player.sendMessage("Purchase successful");
                    player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                } else {
                    player.sendMessage("Not enough coins");
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            if (item.getItemMeta().getCustomModelData() == 10203) {
                if (playersCash.get(uuid) >= 2400) {
                    playersCash.put(uuid, playersCash.get(uuid) - 2400);
                    player.getInventory().addItem(Rapier.createItem());
                    Misc.createInventoryItem(event.getInventory(), Material.SUNFLOWER, 49, 10210, "You have: " + playersCash.get(player.getUniqueId()).toString() + " Coins");
                    player.sendMessage("Purchase successful");
                    player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                } else {
                    player.sendMessage("Not enough coins");
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            if (item.getItemMeta().getCustomModelData() == 10204) {
                if (playersCash.get(uuid) >= 3600) {
                    playersCash.put(uuid, playersCash.get(uuid) - 3600);
                    player.getInventory().addItem(GraveYard.createItem());
                    Misc.createInventoryItem(event.getInventory(), Material.SUNFLOWER, 49, 10210, "You have: " + playersCash.get(player.getUniqueId()).toString() + " Coins");
                    player.sendMessage("Purchase successful");
                    player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                } else {
                    player.sendMessage("Not enough coins");
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
            // Skill point purchase branch (custom model data 10205)
            if (item.getItemMeta().getCustomModelData() == 10205) {
                // Determine how many skill points to buy: 5 for shift-click, or 1 otherwise.
                int multiplier = event.isShiftClick() ? 5 : 1;
                int currentPointsBought = SkillsGUI.getPointsBought(player);
                int totalCost = 0;

                // Calculate the total cost for the next 'multiplier' points.
                for (int i = 0; i < multiplier; i++) {
                    // The cost formula for each point; for example, if the cost is based on how many points have been bought.
                    int costPerPoint = (currentPointsBought + i) * 100;
                    // Add 10% surcharge to the cost.
                    costPerPoint += costPerPoint / 2;
                    totalCost += costPerPoint;
                }

                if (playersCash.get(uuid) >= totalCost) {
                    // Deduct the total cost.
                    playersCash.put(uuid, playersCash.get(uuid) - totalCost);

                    // Increase the skill points and update the points bought.
                    SkillsGUI.setPoints(player, SkillsGUI.getPoints(player) + multiplier);
                    SkillsGUI.setPointsBought(player, currentPointsBought + multiplier);

                    // Update the inventory display.
                    Misc.createInventoryItem(event.getInventory(), Material.SUNFLOWER, 49, 10210, "You have: " + playersCash.get(uuid).toString() + " Coins");
                    Misc.createInventoryItem(event.getInventory(), Material.EXPERIENCE_BOTTLE, 22, 10205, "Buy Points", "Cost: " + totalCost);

                    player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    shopGuiCreate(player);
                } else {
                    player.sendMessage("Not enough coins");
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
        }

        if (item.getType() == Material.DIAMOND) {

            player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
            playersCash.put(uuid, playersCash.get(uuid) + 100);
            player.sendMessage("You have: " + playersCash.get(uuid) + " Coins");
            Misc.createInventoryItem(event.getInventory(), Material.SUNFLOWER, 49, 10210, "You have: " + playersCash.get(player.getUniqueId()).toString() + " Coins");
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
    }
}
}
