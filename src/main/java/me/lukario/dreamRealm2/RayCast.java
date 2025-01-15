package me.lukario.dreamRealm2;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.Particle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RayCast {

    private static Plugin plugin;
    public RayCast(Plugin plugin) {
        this.plugin = plugin;
    }


    public static void raycastWithCritParticle(Player player, float offset, float dmg, float distancee) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        getDirection(player);

        Vector sideOffset;
        if (getDirection(player).equals("North")) {
            sideOffset = new Vector(offset, 0, 0); // Moving to the side in the Z-axis for North
        } else if (getDirection(player).equals("South")) {
            sideOffset = new Vector(-offset, 0, 0); // Moving to the side in the negative Z-axis for South
        } else if (getDirection(player).equals("East")) {
            sideOffset = new Vector(0, 0, offset); // Moving to the side in the X-axis for East
        } else {
            sideOffset = new Vector(0, 0, -offset); // Moving to the side in the negative X-axis for West
        }

        // Offset to make the raycast slightly to the side (adjust values to your preference)

        // Apply the offset to the direction of the raycast
        direction.add(sideOffset);

        // Perform raytrace to detect if any entities are hit
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(eyeLocation, direction, distancee, entity -> entity instanceof LivingEntity && !(entity instanceof Player)); // Max range is 100 blocks

        if (rayTraceResult != null && rayTraceResult.getHitEntity() != null) {
            // Get the hit entity
            Entity hitEntity = rayTraceResult.getHitEntity();

            // Debugging message to check what was hit
//        player.sendMessage(ChatColor.YELLOW + "Hit entity: " + hitEntity.getType().name());

            // Ensure the entity is a mob and not a player
            if (hitEntity instanceof LivingEntity && !(hitEntity instanceof Player)) {
                LivingEntity livingEntity = (LivingEntity) hitEntity;

                // Apply slowness effect to the mob
//            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1)); // Duration: 100 ticks, Amplifier: 1 (slow)

                // Deal 5 damage to the mob
                livingEntity.damage(dmg); // Change this to 5.0 for the intended damage

                // Send message to the player confirming the action
//            player.sendMessage(ChatColor.DARK_GREEN + "You hit a mob with the ray!");
            }
        }
    }


    public static void rayCastWithAnyParticle(Player player, float offset, float dmg, float distancee, String particlee) {
        // Convert particle name to uppercase and validate
        Particle particle;
        try {
            particle = Particle.valueOf(particlee.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid particle type: " + particlee);
            return;
        }

        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        // Adjust direction based on offset


        direction.add(applyOffset(player, offset));

        // Ray trace to detect entities within range
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(
                eyeLocation,
                direction,
                distancee,
                entity -> entity instanceof LivingEntity && !(entity instanceof Player)
        );

        // Damage entity if hit
        if (rayTraceResult != null && rayTraceResult.getHitEntity() != null) {
            Entity hitEntity = rayTraceResult.getHitEntity();
            if (hitEntity instanceof LivingEntity && !(hitEntity instanceof Player)) {
                ((LivingEntity) hitEntity).damage(dmg);
            }
        }

        // Visualize the ray with particles
        for (double i = 0; i <= distancee; i += 0.5) {
            Location particleLocation = eyeLocation.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0);
        }
    }

    public static void rayCastWithAnyPartAndMultMobs(Player player, float offset, float dmg, float distancee, PotionEffectType potionEffectType, String particlee) {
    Particle particle;
    try {
        particle = Particle.valueOf(particlee.toUpperCase());
    } catch (IllegalArgumentException e) {
        player.sendMessage("Invalid particle type: " + particlee);
        return;
    }

    Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection();
    direction.add(applyOffset(player, offset));

    // Expanded hitbox size for the ray
    double hitboxRadius = 1;

    // Track the entities that have already been hit to avoid hitting the same one multiple times
    Set<Entity> hitEntities = new HashSet<>();

    // Trace along the ray
    for (double i = 0; i <= distancee; i += 0.5) {
        Location currentLocation = eyeLocation.clone().add(direction.clone().multiply(i));

        // Visualize the ray with particles
        player.getWorld().spawnParticle(particle, currentLocation, 5, 0.5, 0.5, 0.5, 0);

        // Check nearby entities at the current location
        Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(currentLocation, hitboxRadius, hitboxRadius, hitboxRadius);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity && !(entity instanceof Player) && !hitEntities.contains(entity)) {
                LivingEntity mob = (LivingEntity) entity;
                mob.damage(dmg);
                mob.addPotionEffect(new PotionEffect(potionEffectType, 100, 1)); // Apply the effect
                hitEntities.add(entity); // Add the entity to the hit list
            }
        }
    }
}

    private static Vector applyOffset(Player player, float offset) {
        Vector sideOffset;
        switch (getDirection(player)) {
            case "North":
                sideOffset = new Vector(offset, 0, 0);
                break;
            case "South":
                sideOffset = new Vector(-offset, 0, 0);
                break;
            case "East":
                sideOffset = new Vector(0, 0, offset);
                break;
            case "West":
                sideOffset = new Vector(0, 0, -offset);
                break;
            default:
                sideOffset = new Vector(0, 0, 0);
        }
        return sideOffset;
    }

    private static String getDirection (Player player){
        // Get the player's yaw (rotation around the vertical axis)
        float yaw = player.getLocation().getYaw();

        // Normalize yaw to a value between 0 and 360
        if (yaw < 0) {
            yaw += 360;
        }

        // Determine the cardinal direction based on yaw
        if (yaw >= 45 && yaw < 135) {
            return "East";
        } else if (yaw >= 135 && yaw < 225) {
            return "South";
        } else if (yaw >= 225 && yaw < 315) {
            return "West";
        } else {
            return "North";
        }
    }
        public static void rayCastWithIntervals(Player player, double speed, double maxDistance, Particle particle, float offset) {
        Location startLocation = player.getEyeLocation(); // Start from the player's eye level
        Vector direction = startLocation.getDirection().normalize(); // Ray direction
        direction.add(applyOffset(player, offset));
        World world = player.getWorld();

        new BukkitRunnable() {
            double distanceTraveled = 0; // Track how far the ray has moved
            Location currentLocation = startLocation.clone(); // Current position of the ray

            @Override
            public void run() {
                if (distanceTraveled >= maxDistance) { // Stop if the ray exceeds max distance
                    cancel();
                    return;
                }

                // Move the ray forward by the specified speed
                currentLocation.add(direction.clone().multiply(speed));
                distanceTraveled += speed;

                // Spawn particles to visualize the ray
                player.getWorld().spawnParticle(particle, currentLocation, 1, 0, 0, 0, 0);

                // Check for collision with blocks or entities
                if (world.getBlockAt(currentLocation).getType().isSolid()) {
                    cancel(); // Stop the ray on collision with a block
                    return;
                }

                // Example: Deal damage if hitting an entity
                world.getNearbyEntities(currentLocation, 0.5, 0.5, 0.5).forEach(entity -> {
                    if (!entity.equals(player)) { // Avoid hitting the caster
//                        entity.setFireTicks(60); // Example effect: Set entity on fire
                        LivingEntity livingEntity = (LivingEntity) entity;

                        livingEntity.damage(16,player);

//                        cancel(); // Stop the ray on collision with an entity
                    }
                });
            }
        }.runTaskTimer(plugin, 0, 2); // Run every tick
    }
    static void rayCastWithIntervalsAndParticleSpread(Plugin plugin, Player player, double fromStart, double speed, double maxDistance, Particle particle, float offset, float particleSpread, int particleAmount, double rayCastHitbox) {
        Location startLocation = player.getEyeLocation().add(player.getLocation().getDirection().normalize().multiply(fromStart)); // Start from two blocks in front of the player
        Vector direction = startLocation.getDirection().normalize(); // Ray direction
        direction.add(applyOffset(player, offset)); // Apply any side offset if needed
        World world = player.getWorld();


        new BukkitRunnable() {
            double distanceTraveled = 0; // Track how far the ray has moved
            Location currentLocation = startLocation.clone(); // Current position of the ray

            @Override
            public void run() {
                if (distanceTraveled >= maxDistance) { // Stop if the ray exceeds max distance
                    cancel();
                    return;
                }

                // Move the ray forward by the specified speed
                currentLocation.add(direction.clone().multiply(speed));
                distanceTraveled += speed;

                // Spawn particles to visualize the ray
                player.getWorld().spawnParticle(particle, currentLocation, particleAmount, particleSpread, particleSpread, particleSpread,0);

                // Check for collision with blocks or entities
                if (world.getBlockAt(currentLocation).getType().isSolid()) {
                    cancel(); // Stop the ray on collision with a block
                    return;
                }

                // Example: Deal damage if hitting an entity
                world.getNearbyEntities(currentLocation, rayCastHitbox, rayCastHitbox, rayCastHitbox).forEach(entity -> {
                    if (!entity.equals(player)) { // Avoid hitting the caster
//                        entity.setFireTicks(60); // Example effect: Set entity on fire
                        LivingEntity livingEntity = (LivingEntity) entity;

                        livingEntity.damage(16,player);

//                        cancel(); // Stop the ray on collision with an entity
                    }
                });
            }
        }.runTaskTimer(plugin, 0, 1); // Run every tick
    }

    public static Location performRaycast(Player player, double maxDistance, double stepSize) {
    World world = player.getWorld();
    Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection().normalize();

    // Step through the ray
    for (double distance = 0; distance <= maxDistance; distance += stepSize) {
        Location currentLocation = eyeLocation.clone().add(direction.clone().multiply(distance));

        // Check for nearby entities with a 1-block radius
        for (Entity entity : world.getNearbyEntities(currentLocation, 0.5, 0.5, 0.5)) {
            if (entity instanceof LivingEntity && entity != player) {
                return entity.getLocation(); // Return the location of the hit mob
            }
        }

    }

    return null;
    }

    public static void fireRayWithOffset(Player player, Vector direction, double range, double step, double damage) {
    Location start = player.getEyeLocation(); // Start the ray at the player's eye level
    Location current = start.clone();

    // Calculate the rightward offset
    Vector rightOffset = player.getEyeLocation().getDirection()
        .clone()
        .crossProduct(new Vector(0, 1, 0)) // Cross with up vector to get the right direction
        .normalize()
        .multiply(0.5); // Offset by 0.5 blocks to the right
    current.add(rightOffset); // Apply the offset to the starting position

    Vector increment = direction.clone().normalize().multiply(step); // Small increments in the direction
    Set<Entity> hitEntities = new HashSet<>(); // Track entities already hit by the ray

    new BukkitRunnable() {
        @Override
        public void run() {
            // Spawn particles at the current location
            current.getWorld().spawnParticle(Particle.FLAME, current, 1, 0.2, 0.2, 0.2, 0);

            // Check for entities in a 0.5-block radius
            for (Entity entity : current.getWorld().getNearbyEntities(current, 0.5, 0.5, 0.5)) {
                if (entity instanceof LivingEntity livingEntity && !entity.equals(player) && !hitEntities.contains(entity)) {
                    hitEntities.add(entity); // Mark this entity as hit
                    livingEntity.damage(damage, player); // Damage the entity
                    player.sendMessage(ChatColor.YELLOW + "You hit " + livingEntity.getName() + "!");
                }
            }

            // Check for blocks
            if (current.getBlock().getType().isSolid()) {
                player.sendMessage(ChatColor.RED + "The ray hit a block!");
                this.cancel(); // Stop the ray if it hits a block
                return;
            }

            // Move the ray forward
            current.add(increment);

            // Stop the ray if it exceeds the maximum range
            if (current.distance(start) >= range) {
                player.sendMessage(ChatColor.RED + "The ray reached its maximum range!");
                this.cancel();
            }
        }
    }.runTaskTimer(plugin, 0L, 1L); // Run every tick (50ms)
}


}


