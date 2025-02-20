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
        if (!isHoldingTheCorrectItem(player)) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            spawnArmorStand(player);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            launchArmorStand(player);
        }
    }

    private void spawnArmorStand(Player player) {
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

    private void launchArmorStand(Player player) {
        for (ArmorStand armorStand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
            if (isOwnedArmorStand(armorStand, player)) {
                moveArmorStandToTarget(armorStand, player);
            }
        }
    }

    private boolean isOwnedArmorStand(ArmorStand armorStand, Player player) {
        return armorStand.hasMetadata("RapierOwner") &&
               armorStand.getMetadata("RapierOwner").get(0).asString().equals(player.getUniqueId().toString());
    }

    private void moveArmorStandToTarget(ArmorStand armorStand, Player player) {
        Location targetBlock = player.getTargetBlockExact(50).getLocation(); // Get the block the player is looking at

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!armorStand.isValid()) {
                    this.cancel();
                    return;
                }

                Location currentLoc = armorStand.getLocation();
                Vector direction = targetBlock.clone().subtract(currentLoc).toVector().normalize().multiply(1.5);

                // Move the armor stand
                armorStand.teleport(currentLoc.add(direction));

                // Stop if it reaches the target or collides
                if (currentLoc.distanceSquared(targetBlock) < 2 || isCollidingWithBlock(armorStand)) {
                    triggerExplosionEffects(armorStand);
                    applyRadialDamage(armorStand, player);
                    armorStand.remove();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void applyRadialDamage(ArmorStand armorStand, Player owner) {
        Location loc = armorStand.getLocation();
        double[] damages = {60, 40, 30, 27, 21, 21};

        for (int i = 0; i < damages.length; i++) {
            for (LivingEntity entity : loc.getNearbyLivingEntities(i + 1)) {
                if (entity.equals(owner) || entity instanceof ArmorStand) continue; // Avoid hitting armor stands
                entity.damage(damages[i], owner);
            }
        }
    }

    private void triggerExplosionEffects(ArmorStand armorStand) {
        Location loc = armorStand.getLocation();
        World world = armorStand.getWorld();

        world.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        world.spawnParticle(Particle.EXPLOSION, loc, 10, 2, 2, 2);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 3, 1);
    }

    @EventHandler
    public void onEntityDamagedByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && isHoldingTheCorrectItem(player)) {
            double baseDamage = event.getDamage() + 7;
            event.setDamage((baseDamage * baseDamage) / 10);
        }

        // Prevent Armor Stands from hitting other Armor Stands
        if (event.getDamager() instanceof ArmorStand && event.getEntity() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    private static boolean isHoldingTheCorrectItem(Player player) {
        return isCorrectItem(player.getInventory().getItemInMainHand()) ||
               isCorrectItem(player.getInventory().getItemInOffHand());
    }

    private static boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME));
    }

    private void startTrackingArmorStands(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (tasks.containsKey(playerUUID)) {
            Bukkit.getScheduler().cancelTask(tasks.get(playerUUID));
        }

        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    tasks.remove(playerUUID);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L).getTaskId();

        tasks.put(playerUUID, taskId);
    }

    private boolean isCollidingWithBlock(ArmorStand armorStand) {
        return armorStand.getLocation().getBlock().getType().isSolid();
    }
}
