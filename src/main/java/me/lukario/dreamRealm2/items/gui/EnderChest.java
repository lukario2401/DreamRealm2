package me.lukario.dreamRealm2.items.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderChest implements Listener {

    private static final Map<UUID, Inventory> openInventories = new HashMap<>();
    private final File file;
    private final FileConfiguration config;
    private final Plugin plugin;

    public EnderChest(Plugin plugin) {
        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), "enderchest.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }

        config = YamlConfiguration.loadConfiguration(file);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inv = Bukkit.createInventory(null, 54, "Custom EnderChest");

        if (config.contains(uuid.toString())) {
            for (String key : config.getConfigurationSection(uuid.toString()).getKeys(false)) {
                ItemStack item = config.getItemStack(uuid + "." + key);
                int slot = Integer.parseInt(key);
                inv.setItem(slot, item);
            }
        }
        openInventories.put(uuid, inv);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        Inventory open = openInventories.get(player.getUniqueId());
        if (event.getInventory().equals(open)) {
            // Allow move items
            // nothing special, default behavior
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        Inventory inv = openInventories.remove(uuid);
        if (inv == null) return;
        // Save contents
        config.set(uuid.toString(), null);
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null) {
                config.set(uuid + "." + i, item);
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
