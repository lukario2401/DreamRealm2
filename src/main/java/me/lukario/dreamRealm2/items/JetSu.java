package me.lukario.dreamRealm2.items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class JetSu implements Listener {
    private final Plugin plugin;

    public JetSu(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Jet";//e668c6
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after completing WarThunder";
    private static final Material ITEM_MATERIAL = Material.NETHER_STAR;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.SHARPNESS, 25, false);
            item.setItemMeta(meta);

        }
        return item;
    }

    @EventHandler
    public void jetFlying(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (!isHoldingTheCorrectItem(player)) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_AIR & player.getCooldown(createItem())==0) {
            if (!player.isGliding()) {
                player.setGliding(true);

                // Send a message or play a sound (optional)
                player.getWorld().playSound(player.getLocation(), "entity.phantom.ambient", 1.0f, 1.0f);
            }
            player.setCooldown(createItem(),  2);
            Location startLocation = player.getEyeLocation();
        Vector direction = startLocation.getDirection().normalize(); // Get the direction the player is looking
        double maxDistance = 50; // Maximum range of the bullet
        double stepSize = 0.5; // Distance between each step (smaller for smoother movement)

        // Iterate through points along the ray
        for (double distance = 0; distance < maxDistance; distance += stepSize) {
            Location currentLocation = startLocation.clone().add(direction.clone().multiply(distance));

            // Spawn particles to simulate the bullet
            player.getWorld().spawnParticle(Particle.ENCHANTED_HIT, currentLocation, 1, 0, 0, 0, 0);

            // Check for collision with blocks
            if (currentLocation.getBlock().getType() != Material.AIR) {
                // Create an explosion at the collision point
                currentLocation.getWorld().createExplosion(currentLocation, 2.0F, false, false); // Small explosion
                break; // Stop the ray when a block is hit
            }

            // Check for collision with entities
            for (LivingEntity entity : player.getWorld().getLivingEntities()) {
                if (entity.equals(player)) continue; // Skip the shooter
                if (entity.getBoundingBox().contains(currentLocation.toVector())) {
                    // Damage the entity and add effects
                    entity.damage(27.0, player); // Deal 10 damage
                    entity.setFireTicks(60); // Set the entity on fire for 3 seconds

                    // Spawn particles at the hit point
                    player.getWorld().spawnParticle(Particle.EXPLOSION, currentLocation,10,1,1,1,0);

                    break; // Stop the ray when an entity is hit
                }
            }
        }
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR & player.getCooldown(createItem()) == 0) {
            player.setCooldown(createItem(), 10);

            Location startLocation = player.getLocation();
            Vector direction = new Vector(0, -1, 0); // Direction: straight down
            double maxDistance = 50; // Max distance for the raycast

            // Perform the raycast
            RayTraceResult result = player.getWorld().rayTraceBlocks(
                    startLocation,
                    direction,
                    maxDistance,
                    FluidCollisionMode.NEVER // Ignore fluids (like water and lava)
            );

            if (result != null && result.getHitBlock() != null) {
                // Get the location of the block hit
                Location hitLocation = result.getHitBlock().getLocation();

                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,1,255));
                    // Create an explosion at the hit location
                hitLocation.getWorld().createExplosion(hitLocation.add(0,1,0), 12.0F, false, false); // Power of explosion: 4.0F, no fire, no block damage
                hitLocation.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, hitLocation,1,0,0,0,0);
                hitLocation.getWorld().spawnParticle(Particle.EXPLOSION, hitLocation,10,2,2,2,0);

            }
        }
    }


    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Check if either hand holds the correct item
        if (isCorrectItem(mainHandItem)) {
            return true; // Correct item in main hand
        } else if (isCorrectItem(offHandItem)) {
            return true; // Correct item in off hand
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

}
