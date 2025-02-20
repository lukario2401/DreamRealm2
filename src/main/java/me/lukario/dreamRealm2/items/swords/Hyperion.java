package me.lukario.dreamRealm2.items.swords;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class Hyperion implements Listener {



    private final Plugin plugin;

    // Constructor to accept plugin instance
    public Hyperion(Plugin plugin) {
        this.plugin = plugin;
    }

    private final Set<UUID> knockbackImmunePlayers = new HashSet<>();

    private static final String HYPERION_NAME = net.md_5.bungee.api.ChatColor.of("#e668c6") + "Hyperion";
    private static final String HYPERION_LORE = ChatColor.YELLOW + "Crafted after defeating a ruthless Wither";
    private static final Material MATERIAL_TYPE = Material.IRON_SWORD;

    public static ItemStack createHyperion() {
        ItemStack item = new ItemStack(MATERIAL_TYPE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(HYPERION_NAME); // Uses net.md_5.bungee.api.ChatColor
            meta.setLore(Arrays.asList(HYPERION_LORE));
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static boolean isHoldingHyperion(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != MATERIAL_TYPE) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(HYPERION_NAME))) {
            return false;
        }

        if (meta.getLore() == null) return false;

        for (String loreLine : meta.getLore()) {
            if (ChatColor.stripColor(loreLine).equals(ChatColor.stripColor(HYPERION_LORE))) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    private void hyperionAbility(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isHoldingHyperion(player)) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
              Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection();
    double maxDistance = 12.0;

    // Perform ray tracing to find the first block in the direction
    RayTraceResult rayTraceResult = player.getWorld().rayTraceBlocks(eyeLocation, direction, maxDistance);

    Location teleportLocation;

    if (rayTraceResult != null && rayTraceResult.getHitBlock() != null) {
        // Get the block hit by the ray trace
        Block hitBlock = rayTraceResult.getHitBlock();

        // Determine the location just before the hit block
        teleportLocation = hitBlock.getLocation().subtract(direction);

        // Adjust Y to ensure the player lands on the ground level
        teleportLocation.setY(hitBlock.getY());
    } else {
        // No block was hit; teleport to the maximum distance
        teleportLocation = eyeLocation.clone().add(direction.multiply(maxDistance));
    }

    // Ensure the teleport location is safe
    if (!isSafeTeleportLocation(teleportLocation)) {
        
        return;
    }

    // Teleport the player
    player.teleport(teleportLocation);


            // Teleport
            player.teleport(teleportLocation);


            // Additional effects
            player.getWorld().spawnParticle(Particle.EXPLOSION,player.getLocation(),1);
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 255)); // 1 second of resistance

//            activateAbility(player);
//            player.getWorld().createExplosion(player.getLocation().add(0, 1, 0), 11.5F, false, false);

            double radius = 6.0;
            for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
                if (entity instanceof LivingEntity target) {
                    if (target.equals(player)) continue; // Skip the player themselves
                    target.damage(24, player); // Deal 24 damage
                }
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 400, 4));
        }
    }

    private boolean isSafeTeleportLocation(Location location) {
        World world = location.getWorld();
        if (world == null) return false;

        // Check the block at the location and the block above it
        Material blockType = world.getBlockAt(location).getType();
        Material blockAboveType = world.getBlockAt(location.clone().add(0, 1, 0)).getType();

        // Ensure both blocks are air
        return blockType == Material.AIR && blockAboveType == Material.AIR;
    }

    // Cancel knockback if the player is immune
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (knockbackImmunePlayers.contains(player.getUniqueId())) {
                player.setVelocity(player.getVelocity().multiply(0)); // Cancel knockback by resetting velocity
            }
        }
    }
}
