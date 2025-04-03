package me.lukario.dreamRealm2.items.gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeleportGUI implements Listener {

    private final HashMap<UUID, Location> locationOne = new HashMap<>();
    private final HashMap<UUID, Location> locationTwo = new HashMap<>();
    private final HashMap<UUID, Location> locationThree = new HashMap<>();

    private final JavaPlugin plugin;
    private final File dataFile;
    private final YamlConfiguration config;

    public TeleportGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "player_locations.yml");
        this.config = YamlConfiguration.loadConfiguration(dataFile);
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) return;

        for (String uuidStr : config.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            Location loc1 = config.getLocation(uuidStr + ".location1");
            Location loc2 = config.getLocation(uuidStr + ".location2");
            Location loc3 = config.getLocation(uuidStr + ".location3");
            locationOne.put(uuid, loc1);
            locationTwo.put(uuid, loc2);
            locationThree.put(uuid, loc3);
        }
    }

    public void saveData() {
        config.getKeys(false).forEach(key -> config.set(key, null)); // Clear existing data

        Set<UUID> allUUIDs = new HashSet<>();
        allUUIDs.addAll(locationOne.keySet());
        allUUIDs.addAll(locationTwo.keySet());
        allUUIDs.addAll(locationThree.keySet());

        for (UUID uuid : allUUIDs) {
            String uuidStr = uuid.toString();
            config.set(uuidStr + ".location1", locationOne.get(uuid));
            config.set(uuidStr + ".location2", locationTwo.get(uuid));
            config.set(uuidStr + ".location3", locationThree.get(uuid));
        }

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player locations: " + e.getMessage());
        }
    }

    public static void openTeleportGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, "TeleportGUI");

        // Teleport Location I Items
        ItemStack teleport1 = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = teleport1.getItemMeta();
        meta.setDisplayName("§aTeleport To Location I");
        meta.setCustomModelData(10111);
        meta.setLore(Collections.singletonList("Tp location 1"));
        teleport1.setItemMeta(meta);

        ItemStack setLocation1 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta setMeta1 = setLocation1.getItemMeta();
        setMeta1.setDisplayName("§bSet Teleport Location I");
        setMeta1.setCustomModelData(10112);
        setMeta1.setLore(Collections.singletonList("Set Teleport Location 1"));
        setLocation1.setItemMeta(setMeta1);

        // Teleport Location II Items
        ItemStack teleport2 = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta2 = teleport2.getItemMeta();
        meta2.setDisplayName("§aTeleport To Location II");
        meta2.setCustomModelData(10121);
        meta2.setLore(Collections.singletonList("Tp location 2"));
        teleport2.setItemMeta(meta2);

        ItemStack setLocation2 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta setMeta2 = setLocation2.getItemMeta();
        setMeta2.setDisplayName("§bSet Teleport Location II");
        setMeta2.setCustomModelData(10122);
        setMeta2.setLore(Collections.singletonList("Set Teleport Location 2"));
        setLocation2.setItemMeta(setMeta2);

        // Teleport Location III Items
        ItemStack teleport3 = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta3 = teleport3.getItemMeta();
        meta3.setDisplayName("§aTeleport To Location III");
        meta3.setCustomModelData(10131);
        meta3.setLore(Collections.singletonList("Tp location 3"));
        teleport3.setItemMeta(meta3);

        ItemStack setLocation3 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta setMeta3 = setLocation3.getItemMeta();
        setMeta3.setDisplayName("§bSet Teleport Location III");
        setMeta3.setCustomModelData(10132);
        setMeta3.setLore(Collections.singletonList("Set Teleport Location 3"));
        setLocation3.setItemMeta(setMeta3);

        inventory.setItem(11, teleport1);
        inventory.setItem(20, setLocation1);
        inventory.setItem(13, teleport2);
        inventory.setItem(22, setLocation2);
        inventory.setItem(15, teleport3);
        inventory.setItem(24, setLocation3);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("TeleportGUI")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasCustomModelData()) return;

        int customModelData = meta.getCustomModelData();
        switch (customModelData) {
            case 10111:
                teleportPlayer(player, locationOne.get(player.getUniqueId()), "I");
                break;
            case 10112:
                setLocation(player, locationOne, "I");
                break;
            case 10121:
                teleportPlayer(player, locationTwo.get(player.getUniqueId()), "II");
                break;
            case 10122:
                setLocation(player, locationTwo, "II");
                break;
            case 10131:
                teleportPlayer(player, locationThree.get(player.getUniqueId()), "III");
                break;
            case 10132:
                setLocation(player, locationThree, "III");
                break;
        }
    }

    private void teleportPlayer(Player player, Location location, String locationName) {
        if (location == null) return;
        player.teleport(location);
        player.sendMessage("Teleported to Location " + locationName);
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    private void setLocation(Player player, HashMap<UUID, Location> locationMap, String locationName) {
        locationMap.put(player.getUniqueId(), player.getLocation());
        player.sendMessage("Location " + locationName + " has been set");
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        player.closeInventory();
    }
}