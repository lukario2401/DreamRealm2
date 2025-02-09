package me.lukario.dreamRealm2.items.special.magic;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.swing.*;
import java.util.Arrays;

public class FireWand implements Listener {

    private final Plugin plugin;

    public FireWand(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Fire Wand";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating fire";
    private static final Material ITEM_MATERIAL = Material.NETHER_STAR;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(16);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void fireWandUsed(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isHoldingTheCorrectItem(player)) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Find the initial impact location ONCE when the spell is cast
            Location fixedLocation = findInitialImpactLocation(player);
            if (fixedLocation != null) {
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_MIRROR_MOVE,1,1);
                startFireBeamEffect(player, fixedLocation);
            }
        }
    }

    private Location findInitialImpactLocation(Player player) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (double i = 0; i <= 8; i += 0.5) {
            Location currentLocation = location.clone().add(direction.clone().multiply(i));
            if (currentLocation.getBlock().getType() != Material.AIR) {
                return currentLocation; // Return first non-air block
            }
        }
        // If no block was hit, return max range location
        return location.clone().add(direction.clone().multiply(8));
    }

    private void startFireBeamEffect(Player player, Location fixedLocation) {
        new BukkitRunnable() {
            int runTime = 0;
            final Location persistentLocation = fixedLocation.clone(); // Store as final variable

            @Override
            public void run() {
                if (runTime >= 200) {
                    this.cancel();
                    return;
                }

                // Always use the pre-calculated persistentLocation
                persistentLocation.getWorld().spawnParticle(
                    Particle.SOUL_FIRE_FLAME,
                    persistentLocation,
                    25,
                    0.1,
                    0.1,
                    0.1,
                    0
                );

                if (runTime % 5 == 0) {
                    for (LivingEntity target : persistentLocation.getNearbyLivingEntities(10)) {
                        if (!target.equals(player)) {
                            createDamageBeam(persistentLocation, target, player);
                        }
                    }
                }
                runTime += 1;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    private void createDamageBeam(Location start, LivingEntity target, Player damager) {
        Location end = target.getLocation();
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        Location current = start.clone();

        double maxDistance = start.distance(end);
        for (int i = 0; i < maxDistance * 2; i++) { // Smaller steps for smoother effect
            current.add(direction.clone().multiply(0.5));
            current.getWorld().spawnParticle(Particle.FLAME, current, 1, 0, 0, 0, 0);

            // Check for entities at each step
            for (LivingEntity entity : current.getNearbyLivingEntities(0.5)) {
                if (!entity.equals(damager)) {
                    if (entity.getHealth()>20){
                    entity.setHealth(entity.getHealth()-24);
                    }else{
                        entity.setHealth(0);
                    }
                    entity.playHurtAnimation(1);
                    entity.getWorld().playSound(entity.getLocation(),entity.getHurtSound(),1,1);

                }
            }

            if (current.distanceSquared(start) > maxDistance * maxDistance) {
                break;
            }
        }
    }

    // Rest of helper methods unchanged
    private static boolean isHoldingTheCorrectItem(Player player) {
        return isCorrectItem(player.getInventory().getItemInMainHand()) ||
               isCorrectItem(player.getInventory().getItemInOffHand());
    }

    private static boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME))) return false;
        return meta.getLore() != null && meta.getLore().contains(ITEM_LORE);
    }
}