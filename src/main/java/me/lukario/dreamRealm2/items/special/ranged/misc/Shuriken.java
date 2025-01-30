package me.lukario.dreamRealm2.items.special.ranged.misc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class Shuriken implements Listener {

     private final Plugin plugin;

    public Shuriken(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#C43C3C") + "Shuriken";//D88F07
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating #######";
    private static final Material ITEM_MATERIAL = Material.NETHER_STAR;

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
public void throwShuriken(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (!isHoldingTheCorrectItem(player)) return;

    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
        // Start location (player's eye location)
        Location currentLocation = player.getEyeLocation();

        // Keep track of already targeted mobs to avoid re-targeting
        int maxChains = 1000; // Limit the chaining to avoid infinite loops
        boolean isFirstRun = true;
        for (int i = 0; i < maxChains; i++) {

            LivingEntity nearestMob = findNearestMob(currentLocation, player);

            if (nearestMob == null && isFirstRun) {
                // If no mob is found, perform the red raycast and exit
                performRayCastRed(player, 24);
                return; // Exit early since no mobs are found
            }
            if (nearestMob == null) {
                // If no mob is found, perform the red raycast and exit
                break; // Exit early since no mobs are found
            }

            // Perform raycast to the nearest mob only if the mob's location is valid
            Location mobLocation = nearestMob.getEyeLocation();
            if (mobLocation != null && Double.isFinite(mobLocation.getX()) && Double.isFinite(mobLocation.getY()) && Double.isFinite(mobLocation.getZ())) {
                createRayCast(currentLocation, mobLocation, player);

                // Damage the mob after the raycast
                nearestMob.damage(48.0, player);

                // Update current location for the next iteration
                currentLocation = mobLocation.clone();
            } else {
                // Skip this mob if the location is invalid
                continue;
            }

            if (isFirstRun){
                isFirstRun = false;
            }
        }
    }

    // Right click behavior
    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        performRaycast(player, 24);
    }
}


    private LivingEntity findNearestMob(Location startLocation, Player player) {
        return startLocation.getNearbyLivingEntities(24).stream()
                .filter(entity -> entity instanceof LivingEntity && !entity.equals(player)) // Exclude the player
                .filter(entity -> !entity.isDead()) // Ensure the mob is alive
                .min((e1, e2) -> {
                    double dist1 = startLocation.distanceSquared(e1.getLocation());
                    double dist2 = startLocation.distanceSquared(e2.getLocation());
                    return Double.compare(dist1, dist2);
                })
                .map(entity -> (LivingEntity) entity) // Cast to LivingEntity
                .orElse(null); // Return null if no entities are found
    }



    public void createRayCast(Location start, Location end, Player player) {
        // Get the direction vector from start to end
        Vector direction = end.toVector().subtract(start.toVector()).normalize();

        // Get the total distance between start and end
        double distance = start.distance(end);

        // Increment size (how small each step is)
        double increment = 0.5; // Smaller increments are more precise but can affect performance

        // Current position of the ray (starting at the start location)
        Location current = start.clone();

        // Loop through the distance
        for (double i = 0; i <= distance; i += increment) {
            // Move along the ray direction
            current.add(direction.clone().multiply(increment));

            // Spawn particles along the ray for visualization (optional)
            current.getWorld().spawnParticle(Particle.DUST,current.add(0, 0, 0),10,0, 0, 0,
                        new Particle.DustOptions(Color.RED, 1.0f));

            // Check if the ray hits any nearby entities
            for (Entity entity : current.getWorld().getNearbyEntities(current, 1.0, 1.0, 1.0)) {
                if (entity instanceof LivingEntity && !entity.equals(player)) {
                    // Damage the entity (or any other logic)
                    ((LivingEntity) entity).damage(24.0, player);

                    // Spawn a particle effect on the entity (optional)
                    entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, entity.getLocation(), 1);
                    entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 3, 1);

                    break; // Stop the raycast when it hits an entity
                }
            }
        }
    }

    public void performRaycast(Player player, double maxDistance) {
    // Get the starting position (player's eye location)
    Location start = player.getEyeLocation();

    // Get the direction the player is looking
    Vector direction = start.getDirection();

    // Define the current position (start at the player's eye location)
    Location current = start.clone();

    // Define the maximum distance the ray should travel
    double distanceTraveled = 0;

    // Define the increment size (precision of the raycast)
    double increment = 0.5;

    // Loop through the ray's path
    while (distanceTraveled < maxDistance) {
        if (distanceTraveled+0.5==maxDistance){
            if (current.getBlock().getType() == Material.AIR) {
                // Do something when the ray hits a block
                current.getWorld().createExplosion(current, 4F, false, false); // Small explosion
                current.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,current,1,0,0,0);
                break;
            }
        }
        // Move the current location along the direction vector
        current.add(direction.clone().multiply(increment));
        distanceTraveled += increment;

        // Spawn particles for visualization (optional)
        current.getWorld().spawnParticle(Particle.CRIT, current, 1, 0, 0, 0, 0);

        // Check if the current position hits a block
        if (current.getBlock().getType() != Material.AIR) {
            // Do something when the ray hits a block
            current.getWorld().createExplosion(current, 4F, false, false); // Small explosion
            current.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,current,1,0,0,0);
            break;
        }

        // Check if the ray hits an entity
        for (Entity entity : current.getWorld().getNearbyEntities(current, 1.0, 1.0, 1.0)) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                // Damage the entity (or any other interaction)
//                entity.getWorld().createExplosion(entity,4.5F,false,false);
                // Spawn a particle effect on the entity (optional)

                entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, entity.getLocation(), 1);
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 3, 1);
                ((LivingEntity) entity).damage(23);

                break;
            }
        }
    }
}
public void performRayCastRed(Player player, double maxDistance) {
    // Get the starting position (player's eye location)
    Location start = player.getEyeLocation();

    // Get the direction the player is looking
    Vector direction = start.getDirection();

    // Define the current position (start at the player's eye location)
    Location current = start.clone();

    // Define the maximum distance the ray should travel
    double distanceTraveled = 0;

    // Define the increment size (precision of the raycast)
    double increment = 0.5;

    // Loop through the ray's path
    while (distanceTraveled < maxDistance) {
        if (distanceTraveled+0.5==maxDistance){
            if (current.getBlock().getType() == Material.AIR) {
                // Do something when the ray hits a block
                current.getWorld().createExplosion(current, 4F, false, false); // Small explosion
                current.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,current,1,0,0,0);
                break;
            }
        }
        // Move the current location along the direction vector
        current.add(direction.clone().multiply(increment));
        distanceTraveled += increment;

        // Spawn particles for visualization (optional)
        current.getWorld().spawnParticle(Particle.DUST,current.add(0, 0, 0),10,0, 0, 0,
                        new Particle.DustOptions(Color.RED, 1.0f));

        // Check if the current position hits a block
        if (current.getBlock().getType() != Material.AIR) {
            // Do something when the ray hits a block
            current.getWorld().createExplosion(current, 4F, false, false); // Small explosion
            current.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,current,1,0,0,0);
            break;
        }

        // Check if the ray hits an entity
        for (Entity entity : current.getWorld().getNearbyEntities(current, 1.0, 1.0, 1.0)) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                // Damage the entity (or any other interaction)
//                entity.getWorld().createExplosion(entity,4.5F,false,false);
                // Spawn a particle effect on the entity (optional)

                entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, entity.getLocation(), 1);
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 3, 1);
                ((LivingEntity) entity).damage(23);

                break;
            }
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
