package me.lukario.dreamRealm2.items.swords;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Rapier";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating sword cultivator";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(16);
            meta.addEnchant(Enchantment.SHARPNESS, 10, true);
            meta.addEnchant(Enchantment.LOOTING, 5, true);
            meta.addEnchant(Enchantment.SWEEPING_EDGE, 5, true);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!isHoldingTheCorrectItem(player)) return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            handleRightClick(player);
        } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            handleLeftClick(player);
        }
    }

    private void handleRightClick(Player player) {
        Location spawnLocation = player.getLocation().add(0, 1, 0);
        ArmorStand armorStand = spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class);

        ItemStack customItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = customItem.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(16);
            customItem.setItemMeta(meta);
        }

        armorStand.getEquipment().setHelmet(customItem);
        armorStand.setVisible(false);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setSmall(false);
        armorStand.setMetadata("RapierOwner", new FixedMetadataValue(plugin, player.getUniqueId().toString()));

        startTrackingArmorStands(player);
    }

    private void handleLeftClick(Player player) {
        for (ArmorStand armorStand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (isOwnedArmorStand(armorStand, player)) {
                launchArmorStand(armorStand, player);
            }
        }
    }

    private boolean isOwnedArmorStand(ArmorStand armorStand, Player player) {
        return armorStand.hasMetadata("RapierOwner") &&
               armorStand.getMetadata("RapierOwner").get(0).asString().equals(player.getUniqueId().toString());
    }

    private void launchArmorStand(ArmorStand armorStand, Player player) {
        new BukkitRunnable() {
            final UUID standUUID = armorStand.getUniqueId();

            @Override
            public void run() {
                ArmorStand currentStand = (ArmorStand) armorStand.getWorld().getEntity(standUUID);
                if (currentStand == null || !currentStand.isValid()) {
                    this.cancel();
                    return;
                }

                Location currentLoc = currentStand.getLocation();
                Vector direction = currentLoc.getDirection().normalize();
                Location newLoc = currentLoc.add(direction.multiply(1.5));
                currentStand.teleport(newLoc);

                handleCollisions(currentStand, player, newLoc);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void handleCollisions(ArmorStand armorStand, Player owner, Location location) {
        // Check entity collisions
        for (LivingEntity entity : location.getNearbyLivingEntities(1)) {
            if (entity.equals(owner) || entity.equals(armorStand)) continue;
            applyDamage(entity, owner, 72);
            triggerExplosionEffects(armorStand);
            armorStand.remove();
            return;
        }

        // Check block collisions
        if (isCollidingWithBlock(armorStand)) {
            triggerExplosionEffects(armorStand);
            applyRadialDamage(armorStand, owner);
            armorStand.remove();
        }
    }

    private void applyRadialDamage(ArmorStand armorStand, Player owner) {
        Location loc = armorStand.getLocation();
        double[] damages = {60, 40, 30, 27, 21, 21};

        for (int i = 0; i < damages.length; i++) {
            for (LivingEntity entity : loc.getNearbyLivingEntities(i + 1)) {
                if (entity.equals(owner) || entity.equals(armorStand)) continue;
                applyDamage(entity, owner, damages[i]);
            }
        }
    }

    private void applyDamage(LivingEntity target, Player damager, double amount) {
        target.damage(amount, damager);
    }

    private void triggerExplosionEffects(ArmorStand armorStand) {
        Location loc = armorStand.getLocation();
        World world = armorStand.getWorld();

        world.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        world.spawnParticle(Particle.EXPLOSION, loc, 10, 2, 2, 2);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 3, 1);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 3, 0);
    }

    @EventHandler
    public void onEntityDamagedByEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        if (!isHoldingTheCorrectItem(player)) return;

        double baseDamage = event.getDamage() + 7;
        event.setDamage((baseDamage * baseDamage) / 10);
    }


    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Check if either hand holds the correct item
        if (isCorrectItem(mainHandItem)) {
            return true; // Correct item in main hand
        } else if (isCorrectItem(offHandItem)) {
            return true; // Correct item in of
        }

        return false; // No correct item in either hand
    }

    private static boolean isCorrectItem(ItemStack item) {
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
        return location.getBlock().getType().isSolid() || // Check block at current level
               location.add(0, 1, 0).getBlock().getType().isSolid() || // Check block above
               location.add(0, 2, 0).getBlock().getType().isSolid();  // Check two blocks above
    }
}
