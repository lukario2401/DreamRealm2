package me.lukario.dreamRealm2.items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Rapier implements Listener {

    private final Plugin plugin;
    private final HashMap<UUID, Integer> tasks = new HashMap<>();

    public Rapier(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = net.md_5.bungee.api.ChatColor.of("#D88F07") + "Rapier";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating sword cultivator";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

    public static ItemStack createRapier() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if the player is holding the correct item
        if (isHoldingTheCorrectItem(player)) {
            Action action = event.getAction();

            // Handle right-click actions
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                Location spawnLocation = player.getLocation().add(0, 1, 0);

                // Summon the armor stand
                ArmorStand armorStand = spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class);

                // Customize the armor stand
                armorStand.setVisible(true);
                armorStand.setMarker(true);
                armorStand.setGravity(false);
                armorStand.setSmall(false);

                // Add metadata and tags
                armorStand.setMetadata("Rapier", new FixedMetadataValue(plugin, playerUUID.toString()));
                armorStand.addScoreboardTag("Rapier_" + playerUUID);

                // Start tracking this player's armor stands
                startTrackingArmorStands(player);

                // Notify the player
                player.sendMessage("You have summoned a Rapier armor stand!");
            }

            // Handle left-click actions
                else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
    player.sendMessage("Left click detected!");

    for (ArmorStand armorStand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
        if (armorStand.getScoreboardTags().contains("Rapier_" + playerUUID)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!armorStand.isValid()) {
                        this.cancel(); // Stop the task if the armor stand is no longer valid
                        return;
                    }

                    // Get the armor stand's current location and direction
                    Location currentLocation = armorStand.getLocation();
                    Vector direction = currentLocation.getDirection().normalize();

                    if (direction.length() == 0) {
                        direction = new Vector(0, 0, 1); // Fallback if no direction
                    }

                    // Calculate the new location by moving in the direction
                    Location newLocation = currentLocation.add(direction.multiply(1.5)); // Adjust speed here

                    // Teleport the armor stand to the new location
                    armorStand.teleport(newLocation);

                    // Check if the armor stand has hit a block
                    if (isCollidingWithBlock(armorStand)) {
                        for (LivingEntity livingEntity : currentLocation.getNearbyLivingEntities(6)) {
                            // Check if the entity is not the player who triggered the event
                            if (!livingEntity.equals(player)) {
                                livingEntity.damage(21, player);
                            }
                        }
                    }
                    if (isCollidingWithBlock(armorStand)) {
                        // Create an explosion at the armor stand's location
                        newLocation.getWorld().createExplosion(newLocation.add(0,1,0), 11.5F, false, false);
                        armorStand.remove(); // Remove the armor stand after explosion
                        this.cancel(); // Stop the task
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L); // Schedule task to run every tick
        }
    }
}
        }
    }


    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != ITEM_MATERIAL) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME))) {
            return false;
        }

        if (meta.getLore() == null) return false;

        for (String loreLine : meta.getLore()) {
            if (ChatColor.stripColor(loreLine).equals(ChatColor.stripColor(ITEM_LORE))) {
                return true;
            }
        }
        return false;
    }

    private void startTrackingArmorStands(Player player) {
        UUID playerUUID = player.getUniqueId();

        // Cancel any existing tasks for this player
        if (tasks.containsKey(playerUUID)) {
            Bukkit.getScheduler().cancelTask(tasks.get(playerUUID));
        }

        // Start a new repeating task
        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                // Check if the player is still online
                if (!player.isOnline()) {
                    this.cancel();
                    tasks.remove(playerUUID);
                    return;
                }

                // Get all armor stands with the player's UUID in the tag
                for (ArmorStand armorStand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
                    if (armorStand.getScoreboardTags().contains("Rapier_" + playerUUID)) {
                        // Update the armor stand's direction to match the player's gaze
                        Location eyeLocation = player.getEyeLocation();
                        Vector direction = eyeLocation.getDirection();

                        Location armorStandLocation = armorStand.getLocation();
                        armorStandLocation.setDirection(direction);
                        armorStand.teleport(armorStandLocation);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L).getTaskId(); // Run every tick (1L)

        // Store the task ID for this player
        tasks.put(playerUUID, taskId);
    }
    private boolean isCollidingWithBlock(ArmorStand armorStand) {
        Location location = armorStand.getLocation();
        return location.getBlock().getType().isSolid(); // Check if the block is solid
    }
}
