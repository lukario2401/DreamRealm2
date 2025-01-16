package me.lukario.dreamRealm2.items;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import net.md_5.bungee.api.ChatColor;

public class Pyromancer implements Listener {

    private final Map<Player, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 5000; // 5 seconds cooldown to prevent multiple explosions
    private final Map<Player, Boolean> isExplosionMode = new HashMap<>();


    private final Plugin plugin;

    public Pyromancer(Plugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createPyromancer() {
        ItemStack item = new ItemStack(Material.FLINT_AND_STEEL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.of("#FFA500") + "Pyromancer");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Problems one solution " +ChatColor.DARK_RED + " Explosion"));
            meta.setCustomModelData(4);
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }


    public void setPlayerMetadata(Player player, String tag) {
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, tag), PersistentDataType.BYTE, (byte) 1);
    }

    public boolean hasPlayerMetadata(Player player, String tag) {
        return player.getPersistentDataContainer().has(new NamespacedKey(plugin, tag), PersistentDataType.BYTE);
    }

    public void removePlayerMetadata(Player player, String tag) {
        player.getPersistentDataContainer().remove(new NamespacedKey(plugin, tag));
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

       if (checkIfPlayerIsHoldingCorrectItem(player)){

        boolean isSneaking = player.isSneaking();
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                setPlayerMetadata(player, "fl");
                setPlayerMetadata(player, isSneaking ? "sl" : "sr");
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                setPlayerMetadata(player, "fr");
                setPlayerMetadata(player, isSneaking ? "sr" : "sl");
                break;
        }

        if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sr")) {

            // Shoot falling blocks in direction that player is facing, blocks will explose

            throwBoulder(player);

//            player.sendMessage(ChatColor.RED + " Left click");
            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sr");

        } else if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sl")) {

            //where player is looking draw a ray cast if it hits a mob then drop a meteor on the mob
//            player.sendMessage(ChatColor.RED + "Sneak Left click");

            RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 128,entity -> entity instanceof LivingEntity && !(entity instanceof Player));

            if (result != null && result.getHitEntity() != null) {
                Entity hitEntity = result.getHitEntity();
                // Make sure the entity is a LivingEntity
                if (hitEntity instanceof LivingEntity) {
                    LivingEntity hitLivingEntity = (LivingEntity) hitEntity;
                    dropMeteorOnEntity(hitLivingEntity);
                }
            }


            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sl");

        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sl")) {

            // Bonzo Staff
            throwKnockbackProjectile(player);
//            player.sendMessage(ChatColor.RED + "Right click");
            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sl");
            event.setCancelled(true);

        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sr")) {

            // Explosion upon taking damage


            activateExplosionMode(player);
            player.sendMessage(ChatColor.RED + "Sneak Right click");
            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sr");
        }
        }
    }

    private static boolean checkIfPlayerIsHoldingCorrectItem(Player player) {
    ItemStack item = player.getInventory().getItemInMainHand();
    if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
        return false;
    }

    // Validate main hand item
    ItemMeta meta = item.getItemMeta();
    if (!meta.getDisplayName().equals(ChatColor.of("#FFA500") + "Pyromancer") ||
        meta.getLore() == null ||
        !meta.getLore().contains(ChatColor.YELLOW + "Problems one solution " +ChatColor.DARK_RED + " Explosion")) {
        return false;
    }

    return true;
    }

    private void throwBoulder(Player player) {
    Location startLocation = player.getEyeLocation();
    World world = player.getWorld();
    Vector direction = startLocation.getDirection().normalize();
    Random random = new Random();

    Material[] materials = {Material.DIRT, Material.COBBLESTONE, Material.STONE};
    int numberOfBlocks = 24;

    for (int i = 0; i < numberOfBlocks; i++) {
        Material material = materials[random.nextInt(materials.length)];
        FallingBlock fallingBlock = world.spawnFallingBlock(
                startLocation.clone().add(0, 1, 0),
                material.createBlockData()
        );

        // Velocity for forward motion, along with random spread
        Vector velocity = direction.clone().multiply(3) // Strong forward velocity (you can adjust the factor)
                .add(new Vector(
                        (random.nextDouble() - 0.5) * 0.5, // Horizontal spread
                        (random.nextDouble() - 0.3) * 0.5, // Vertical spread
                        0 // No backward spread, focusing on forward motion
                ));

        fallingBlock.setVelocity(velocity);
        fallingBlock.setDropItem(false);
        fallingBlock.setInvulnerable(true);

        // Prevent knockback using a scheduler
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!fallingBlock.isValid()) {
                    cancel();
                    return;
                }
                // Continuously reset the velocity to counteract knockback
            }
        }.runTaskTimer(plugin, 0, 1); // Run every tick

        // Schedule explosion
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fallingBlock.isDead() || fallingBlock.isOnGround()) {
//                    world.createExplosion(fallingBlock.getLocation(), 11.5F, false, false); // Explosion effect

                    world.spawnParticle(Particle.EXPLOSION,fallingBlock.getLocation(),2);
                    world.playSound(fallingBlock.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 1);

                    for (Entity entity : world.getEntities()) {
                    // Check if the entity is within the radius, not the player, and is not the falling boulder
                    if (entity instanceof LivingEntity && entity != player && entity.getLocation().distance(fallingBlock.getLocation()) <= 6) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.damage(32); // Adjust the damage as needed
                    }
                }

                    fallingBlock.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1); // Monitor landing
    }
}
   private void dropMeteorOnEntity(LivingEntity target) {
    // Location where the meteor will drop (on the mob)
    Location targetLocation = target.getLocation().add(0, 5, 0); // Drop the meteor slightly above the mob

    // Spawn a meteor (can use a custom effect like falling block or just an explosion)
    FallingBlock meteor = target.getWorld().spawnFallingBlock(targetLocation, Material.MAGMA_BLOCK.createBlockData());
    meteor.setGravity(true); // Make sure the meteor falls naturally
    meteor.setDropItem(false); // Don't drop any items on impact
    meteor.setInvulnerable(true); // Prevent it from taking damage

    // Apply some velocity to the meteor so it falls towards the mob
    Vector direction = target.getLocation().subtract(meteor.getLocation()).toVector().normalize();
    meteor.setVelocity(direction.multiply(1.5)); // Adjust the speed

    // Schedule explosion when the meteor hits the ground
    new BukkitRunnable() {
        @Override
        public void run() {
            if (meteor.isOnGround()) {
                // Check if the meteor has been tagged already (to avoid multiple explosions)
                if (!meteor.getPersistentDataContainer().has(new NamespacedKey(plugin, "touched_ground"), PersistentDataType.BYTE)) {
                    // Tag the meteor as having touched the ground
                    meteor.getPersistentDataContainer().set(new NamespacedKey(plugin, "touched_ground"), PersistentDataType.BYTE, (byte) 1);

                    // Create an explosion at the meteor's location
                    Location meteorLocation = meteor.getLocation().add(0, 1, 0); // Slight adjustment to explosion height
                    meteor.remove(); // Remove meteor before explosion

                    meteor.getWorld().createExplosion(meteorLocation, 12.5F, false, true); // Explosion with power of 12.5F

                    meteorLocation = meteorLocation.add(0, -1, 0);

                    meteor.getWorld().createExplosion(meteorLocation, 12.5F, false, true); // Explosion with power of 12.5F

                    meteorLocation = meteorLocation.add(0, -1, 0);

                    meteor.getWorld().createExplosion(meteorLocation, 12.5F, false, true); // Explosion with power of 12.5F

                    meteorLocation = meteorLocation.add(0, 1, 0);

                    // Run the logic to kill "Magma" items after the explosion
                    Location finalMeteorLocation = meteorLocation;
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        // Find nearest item in a specific radius around the explosion
                        double radius = 5.0; // Set the radius to search for items
                        Item nearestItem = null;
                        double nearestDistance = Double.MAX_VALUE;

                        for (Entity entity : finalMeteorLocation.getWorld().getNearbyEntities(finalMeteorLocation, radius, radius, radius)) {
                            if (entity instanceof Item) {
                                Item item = (Item) entity;
                                // Check if the item is of type MAGMA_BLOCK
                                if (item.getItemStack().getType() == Material.MAGMA_BLOCK) {
                                    double distance = entity.getLocation().distance(finalMeteorLocation);
                                    if (distance < nearestDistance) {
                                        nearestItem = item;
                                        nearestDistance = distance;
                                    }
                                }
                            }
                        }

                        if (nearestItem != null) {
                            nearestItem.remove(); // Remove the item if it's the nearest "Magma" item
                        }
                    });
                }
            }
        }
    }.runTaskTimer(plugin, 0, 1); // Check every tick until the meteor hits the ground
}

    private void throwKnockbackProjectile(Player player) {
    // Get the player's location and direction
    Location startLocation = player.getEyeLocation();
    Vector direction = startLocation.getDirection().normalize();

    // Create a throwable projectile (snowball in this case)
    Snowball projectile = player.getWorld().spawn(startLocation, Snowball.class);
    projectile.setVelocity(direction.multiply(1.5));  // Adjust the speed

    // Schedule a task to apply knockback when the projectile lands
    new BukkitRunnable() {
        @Override
        public void run() {
            if (!projectile.isValid()) {
                cancel();
                return;
            }

            // If the projectile hits the ground or collides with an entity
            if (projectile.isOnGround() || projectile.getLocation().getBlock().getType().isSolid()) {
                // Get the location of where the projectile landed
                Location landLocation = projectile.getLocation();
                // Apply knockback to all entities within a radius
                applyKnockbackToNearbyEntities(landLocation, 3, player);  // Radius of 6 blocks

                // Remove the projectile after it lands
                projectile.remove();
                cancel();
            }
        }
    }.runTaskTimer(plugin, 0, 1); // Check every tick if the projectile has landed
}

@EventHandler
public void onProjectileHit(ProjectileHitEvent event) {
    Projectile projectile = event.getEntity();
    if (projectile instanceof Snowball) {
        Location landLocation = projectile.getLocation();
        Player throwingPlayer = (Player) projectile.getShooter(); // Get the player who threw the projectile

        // Apply knockback to all entities within a radius
        applyKnockbackToNearbyEntities(landLocation, 3, throwingPlayer);

        // Remove the projectile after it lands
        projectile.remove();
    }
}

private void applyKnockbackToNearbyEntities(Location location, double radius, Player throwingPlayer) {
    for (Entity entity : location.getWorld().getEntities()) {
        // Check if the entity is within the radius, is not the throwing player, and is a living entity
        if (entity instanceof LivingEntity && entity != throwingPlayer) {
            if (entity.getLocation().distance(location) <= radius) {
                // Apply knockback to the entity
                Vector knockbackDirection = entity.getLocation().subtract(location).toVector().normalize();
                knockbackDirection.setY(0.2); // Slight vertical knockback
                entity.setVelocity(knockbackDirection.multiply(2.5)); // Adjust knockback force
            }
        }
    }

    // Apply knockback to the throwing player if they are within the radius
    if (throwingPlayer.getLocation().distance(location) <= radius) {
        Vector knockbackDirection = throwingPlayer.getLocation().subtract(location).toVector().normalize();
        knockbackDirection.setY(0.2); // Slight vertical knockback
        throwingPlayer.setVelocity(knockbackDirection.multiply(2.5)); // Adjust knockback force
    }
}

//here4\\

     public void activateExplosionMode(Player player) {
        isExplosionMode.put(player, true);
        player.sendMessage(ChatColor.GREEN + "Explosion mode activated! You will explode if damaged.");

        // Deactivate explosion mode after 5 seconds (100 ticks)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isExplosionMode.getOrDefault(player, false)) {
                    isExplosionMode.put(player, false);
                    player.sendMessage(ChatColor.RED + "Explosion mode ended.");
                }
            }
        }.runTaskLater(plugin, 100); // 5 seconds = 100 ticks
    }

    // Event handler for when a player takes damage
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Check if the player is in explosion mode
        if (isExplosionMode.getOrDefault(player, false)) {
            // Trigger explosion when the player takes damage
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,1,255));
            player.getWorld().createExplosion(player.getLocation().add(0,1,0), 11.5F, false, false); // Explosion with power 4

            // Optionally, deal some extra damage or perform any other action before cancelling the event
            event.setCancelled(true); // Cancel the damage event to prevent player from taking damage
            player.sendMessage(ChatColor.RED + "You exploded due to taking damage in explosion mode!");

            // Optionally deactivate explosion mode if you want it to be a one-time effect
//            isExplosionMode.put(player, false);st
        }
    }

}
