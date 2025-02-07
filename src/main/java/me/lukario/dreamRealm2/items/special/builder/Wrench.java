package me.lukario.dreamRealm2.items.special.builder;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Objects;

public class Wrench implements Listener {

    private final Plugin plugin;

    public Wrench(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Wrench";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ######";
    private static final Material ITEM_MATERIAL = Material.AMETHYST_SHARD;
    private static boolean runTheRayCast = true;

    public static ItemStack createItem() {
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
public void wrenchUsed(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (!isHoldingTheCorrectItem(player)) return;

    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        // Get player's horizontal facing direction
        Location playerLoc = player.getLocation();
        Vector direction = new Vector(playerLoc.getDirection().getX(), 0, playerLoc.getDirection().getZ()).normalize();

        // Create armor stand facing player's initial direction
        ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(playerLoc, EntityType.ARMOR_STAND);
        armorStand.setArms(true);
        armorStand.setBasePlate(false);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.getEquipment().setHelmet(new ItemStack(Material.ENDER_EYE));

        // Set initial rotation
        Location armorStandLoc = playerLoc.clone();
        armorStandLoc.setDirection(direction);
        armorStand.teleport(armorStandLoc);

        final float baseYaw = armorStandLoc.getYaw(); // Store initial facing

        new BukkitRunnable() {
            double offsetAngle = 0; // Start centered
            boolean increasing = true;
            int ticksLived = 0;

            @Override
            public void run() {
                if (!armorStand.isValid() || ticksLived++ >= 1200) {
                    armorStand.remove();
                    cancel();
                    return;
                }

                // Update rotation with base yaw + offset
                Location newLoc = armorStand.getLocation();
                newLoc.setYaw((float) (baseYaw + offsetAngle));
                armorStand.teleport(newLoc);

                // Oscillate between -90° and +90°
                if (increasing) {
                    offsetAngle += 1.5;
                    if (offsetAngle >= 90) increasing = false;
                } else {
                    offsetAngle -= 1.5;
                    if (offsetAngle <= -90) increasing = true;
                }

                performRayCast(armorStand, player);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}

    private void performRayCast(ArmorStand armorStand, Player player) {
        Location start = armorStand.getEyeLocation();
        Vector direction = armorStand.getLocation().getDirection();

        boolean isLivingEntityDetected = false;
        LivingEntity targetEntity = null;

        if (!isLivingEntityDetected){
            for (double i = 0; i <= 8; i += 0.1) {
                Location point = start.clone().add(direction.clone().multiply(i));

                for (LivingEntity entity : point.getNearbyLivingEntities(0.3)) {
                    if (!entity.equals(player) && entity.getType() != EntityType.ARMOR_STAND) {
                        isLivingEntityDetected = true;
                        targetEntity = entity;
                        break;
                    }
                }

                if (isLivingEntityDetected) {
                    point.getWorld().spawnParticle(Particle.FLAME, point.add(0, 0.2, 0), 1, 0, 0, 0, 0);
                    break;
                } else {
                    if (runTheRayCast){
                        point.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, point.add(0, 0.2, 0), 1, 0, 0, 0, 0);
                    }
                }
            }
        }
        if (isLivingEntityDetected && targetEntity != null) {
            startRaycast(armorStand, targetEntity);
            runTheRayCast = false;
        }
    }

    private void startRaycast(Entity entity1, Entity entity2) {
    new BukkitRunnable() {
        @Override
        public void run() {
            if (!entity1.isValid() || !entity2.isValid()) {
                this.cancel();
                runTheRayCast = true;
                return;
            }

            Location start = entity1.getLocation();
            Location end = entity2.getLocation();

            // Calculate angle between armor stand and target
            double dx = end.getX() - start.getX();
            double dz = end.getZ() - start.getZ();
            double targetYaw = Math.toDegrees(Math.atan2(-dx, dz)); // Minecraft's yaw calculation
            double armorStandYaw = start.getYaw();

            // Normalize angle difference to [-180, 180]
            double yawDifference = (targetYaw - armorStandYaw + 360) % 360;
            if (yawDifference > 180) yawDifference -= 360;

            // Cancel if target is outside 90° cone
            if (Math.abs(yawDifference) > 90) {
                this.cancel();
                runTheRayCast = true;
                return;
            }

            // Existing distance check
            double distance = start.distance(end);
            if (distance > 50) {
                this.cancel();
                runTheRayCast = true;
                return;
            }

            // Draw particles
            Vector direction = end.toVector().subtract(start.toVector()).normalize();
            for (double i = 0; i < distance; i += 0.1) {
                Location particleLocation = start.clone().add(direction.clone().multiply(i));
                particleLocation.getWorld().spawnParticle(
                    Particle.FLAME,
                    particleLocation.add(0, 0.2, 0),
                    1, 0, 0, 0, 0
                );
            }
        }
    }.runTaskTimer(plugin, 0L, 1L);
}

    private static boolean isHoldingTheCorrectItem(Player player) {
        return isCorrectItem(player.getInventory().getItemInMainHand()) ||
               isCorrectItem(player.getInventory().getItemInOffHand());
    }

    private static boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME))) {
            return false;
        }

        return meta.getLore() != null && meta.getLore().contains(ITEM_LORE);
    }
}