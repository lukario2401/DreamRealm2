package me.lukario.dreamRealm2;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ShadowDance implements Listener {
    int isArmorStandBuffed = 0;
    private final Plugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final ProtocolManager protocolManager;

    public ShadowDance(Plugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager(); // Initialize ProtocolManager
    }

    public static ItemStack createShadowDance() {
        ItemStack shadowDance = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = shadowDance.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.BLACK + "Shadow Dance");
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "A beautiful dance turned into a deadly weapon"));
            meta.setCustomModelData(8);
            shadowDance.setItemMeta(meta);
        }
        return shadowDance;
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        // Check if the damager is a player
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            // Get the item in the player's main hand
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
                return;
            }

            ItemMeta meta = item.getItemMeta();
            if (!meta.getDisplayName().equals(ChatColor.BLACK + "Shadow Dance") ||
                meta.getLore() == null ||
                !meta.getLore().contains(ChatColor.YELLOW + "A beautiful dance turned into a deadly weapon")) {
                return;
            }

            Entity hitEntity = event.getEntity();

            // Check if the entity is a LivingEntity
            if (hitEntity instanceof LivingEntity) {

                List<LivingEntity> nearbyEntities = getNearbyLivingEntitiesAroundPlayer(hitEntity, 3);

                for (LivingEntity entity : nearbyEntities) {
                    if(entity != event.getDamager()){
                        entity.damage(21.0);
                    }
                }

                LivingEntity livingEntity = (LivingEntity) hitEntity;

                spawnAnimatedSlashEntity(livingEntity);

                // Apply extra damage
                double extraDamage = 12;
                event.setDamage(event.getDamage() + extraDamage);
                event.setDamage(event.getFinalDamage() * 2);

                if (((Player) event.getDamager()).getHealth() + event.getFinalDamage()/5 > 20){
                    ((Player) event.getDamager()).setHealth(20);
                }else{
                    ((Player) event.getDamager()).setHealth(((Player) event.getDamager()).getHealth() + event.getFinalDamage()/5);
                }

                PotionEffectType effectType = PotionEffectType.SLOWNESS;
                PotionEffectType effectTypee = PotionEffectType.POISON;
                PotionEffectType effectTypeee = PotionEffectType.MINING_FATIGUE;
                PotionEffectType effectTypeeee = PotionEffectType.WEAKNESS;
                PotionEffectType effectTypeeeee = PotionEffectType.SLOWNESS;
                livingEntity.addPotionEffect(new PotionEffect(effectType, 300, 1));
                livingEntity.addPotionEffect(new PotionEffect(effectTypee, 300, 1));
                livingEntity.addPotionEffect(new PotionEffect(effectTypeee, 600, 1));
                livingEntity.addPotionEffect(new PotionEffect(effectTypeeee, 300, 1));
                livingEntity.addPotionEffect(new PotionEffect(effectTypeeeee, 100, 25));
                spawnBloodParticles(livingEntity);
                // player.sendMessage(ChatColor.RED + "You dealt massive damage with Shadow Dance!");
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.getDisplayName().equals(ChatColor.BLACK + "Shadow Dance") ||
            meta.getLore() == null ||
            !meta.getLore().contains(ChatColor.YELLOW + "A beautiful dance turned into a deadly weapon")) {
            return;
        }

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

//            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2.0f, 1.0f);
//            player.sendMessage(ChatColor.RED + "Left click");
            spawnAnimatedSlash(player);
            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sr");

        } else if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sl")) {

//            player.sendMessage(ChatColor.RED + "Sneak Left click");

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2.0f, 1.0f);
//            RayCast rayCast = new RayCast(plugin); <-- This is wrong
            RayCast rayCast = new RayCast(plugin); //  <-- this is correct
            rayCast.rayCastWithIntervals(player,1,10,Particle.SWEEP_ATTACK,0.40F);
            rayCast.rayCastWithIntervals(player,1,10,Particle.SWEEP_ATTACK,0.20F);
            rayCast.rayCastWithIntervals(player,1,10,Particle.SWEEP_ATTACK,0F);
            rayCast.rayCastWithIntervals(player,1,10,Particle.SWEEP_ATTACK,-0.20F);
            rayCast.rayCastWithIntervals(player,1,10,Particle.SWEEP_ATTACK,-0.40F);

            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sl");

        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sl")) {

            spawnHuntingArmorStand(location,player);
            player.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 2.0f);


//            player.sendMessage(ChatColor.RED + "Right click");
            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sl");

        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sr")) {

//            player.sendMessage(ChatColor.RED + "Sneak Right click");

            isArmorStandBuffed = 200;
            player.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0f, 1.0f);


            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sr");
        }
    }

    public void spawnAnimatedSlash(Player player) {
        World world = player.getWorld();

    // Number of animation frames and slash width
    int frames = 15;
    double arcWidth = 1.5;

    // Capture the initial player location and direction
    Location initialLocation = player.getLocation();
    Vector initialDirection = initialLocation.getDirection().normalize();

    // Calculate the static base location for the slash
    Location baseLocation = initialLocation.clone().add(initialDirection.multiply(0)).add(0, 2, 0);

    // Create the animation task
    new BukkitRunnable() {
        int currentFrame = 0;

        @Override
        public void run() {
            if (currentFrame >= frames) {
                this.cancel();
                return;
            }

            // Calculate the slash position for this frame (rotated by 90 degrees)
            double progress = (double) currentFrame / frames;
            double angle = Math.PI / 4 - (Math.PI / 2) * progress; // Move from 45° to -45°
            double offsetX = Math.cos(angle) * arcWidth;
            double offsetY = -progress * 1.5; // Progressively lower the slash
            double offsetZ = Math.sin(angle) * arcWidth;

            // Rotate offsets: Apply additional 90-degree rotation around the Y-axis
            Vector offset = rotateAroundAxisY(new Vector(offsetX, offsetY, offsetZ), 90); // Rotate by 90 degrees
            offset = rotateAroundAxisY(offset, getYaw(initialLocation)); // Align to player's initial yaw
            Location particleLocation = baseLocation.clone().add(offset);

            // Spawn black and gray particles
            Particle.DustOptions blackDust = new Particle.DustOptions(Color.BLACK, 1.0f);
            Particle.DustOptions grayDust = new Particle.DustOptions(Color.GRAY, 0.8f);

            // Black particle trail
            world.spawnParticle(Particle.DUST, particleLocation, 2, blackDust);

            // Gray particle trail (slightly behind for a layered effect)
            world.spawnParticle(Particle.DUST, particleLocation.clone().subtract(0.1, 0.1, 0.1), 1, grayDust);

            currentFrame++;
        }
    }.runTaskTimer(plugin, 0, 1); // Run every 2 ticks for smooth animation
}
public void spawnAnimatedSlashEntity(LivingEntity entity) {
        World world = entity.getWorld();

    // Number of animation frames and slash width
    int frames = 15;
    double arcWidth = 1.5;

    // Capture the initial player location and direction
    Location initialLocation = entity.getLocation();
    Vector initialDirection = initialLocation.getDirection().normalize();

    // Calculate the static base location for the slash
    Location baseLocation = initialLocation.clone().add(initialDirection.multiply(0)).add(0, 2, 0);

    // Create the animation task
    new BukkitRunnable() {
        int currentFrame = 0;

        @Override
        public void run() {
            if (currentFrame >= frames) {
                this.cancel();
                return;
            }

            // Calculate the slash position for this frame (rotated by 90 degrees)
            double progress = (double) currentFrame / frames;
            double angle = Math.PI / 4 - (Math.PI / 2) * progress; // Move from 45° to -45°
            double offsetX = Math.cos(angle) * arcWidth;
            double offsetY = -progress * 1.5; // Progressively lower the slash
            double offsetZ = Math.sin(angle) * arcWidth;

            // Rotate offsets: Apply additional 90-degree rotation around the Y-axis
            Vector offset = rotateAroundAxisY(new Vector(offsetX, offsetY, offsetZ), 90); // Rotate by 90 degrees
            offset = rotateAroundAxisY(offset, getYaw(initialLocation)); // Align to player's initial yaw
            Location particleLocation = baseLocation.clone().add(offset);

            // Spawn black and gray particles
            Particle.DustOptions blackDust = new Particle.DustOptions(Color.BLACK, 1.0f);
            Particle.DustOptions grayDust = new Particle.DustOptions(Color.GRAY, 0.8f);

            // Black particle trail
            world.spawnParticle(Particle.DUST, particleLocation, 2, blackDust);

            // Gray particle trail (slightly behind for a layered effect)
            world.spawnParticle(Particle.DUST, particleLocation.clone().subtract(0.1, 0.1, 0.1), 1, grayDust);

            currentFrame++;
        }
    }.runTaskTimer(plugin, 0, 1); // Run every 2 ticks for smooth animation
}

    // Rotate a vector around the Y-axis by a given yaw in degrees
    private Vector rotateAroundAxisY(Vector vector, double yawDegrees) {
        double yawRadians = Math.toRadians(yawDegrees);
        double cosYaw = Math.cos(yawRadians);
        double sinYaw = Math.sin(yawRadians);

        double x = vector.getX() * cosYaw - vector.getZ() * sinYaw;
        double z = vector.getX() * sinYaw + vector.getZ() * cosYaw;

        return new Vector(x, vector.getY(), z);
    }

    // Get the player's yaw in degrees
    private double getYaw(Location location) {
        return location.getYaw();
    }
private void spawnBloodParticles(LivingEntity entity) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 600 || entity.isDead()) { // 100 ticks = 5 seconds
                    cancel();
                    return;
                }

                // Spawn redstone particle (looks like blood)
                entity.getWorld().spawnParticle(
                    Particle.DUST,
                    entity.getLocation().add(0, 1, 0), // Adjust Y-axis for head height
                    10, // Number of particles
                    0.2, 0.5, 0.2, // Spread in X, Y, Z
                    new Particle.DustOptions(Color.BLACK, 1.0f) // Blood red color
                );

                ticks += 5; // Run every 5 ticks (0.25 seconds)
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    public void spawnHuntingArmorStand(Location location, Player player) {
    World world = location.getWorld();
    if (world == null) return;

    // Spawn the armor stand
    ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
    armorStand.setArms(true);
    armorStand.setBasePlate(false);
    armorStand.setInvisible(false);
    armorStand.setInvulnerable(true);
    armorStand.setMarker(true); // Allow collisions for proper positioning
    armorStand.setGravity(false); // Use gravity for natural falling
//    armorStand.setCustomName("Saint");
//    armorStand.setCustomNameVisible(true);

    // Equip the armor stand with Netherite gear
    armorStand.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
    armorStand.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
    armorStand.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
    armorStand.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
    armorStand.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

    // Task to make the armor stand hunt mobs
    new BukkitRunnable() {
        int lifetime = 0;

        @Override
        public void run() {
            if (lifetime >= 30 * 20) { // 30 seconds in ticks
                armorStand.remove();
                cancel();
                return;
            }

            // Find the nearest mob
            LivingEntity target = findNearestMob(armorStand);
            Location armorStandLocation = armorStand.getLocation();

            Location belowLocation = armorStandLocation.clone().subtract(0, 0.1, 0);
            if (belowLocation.getBlock().getType() == Material.AIR) {
                // Move the armor stand down only if not already at a lower Y-coordinate
                armorStand.teleport(armorStandLocation.add(0, -0.1, 0));
            }
            Location aboveLocation = armorStandLocation.clone().subtract(0, -0.1, 0);
            if (aboveLocation.getBlock().getType() != Material.AIR) {
                // Move the armor stand down only if not already at a lower Y-coordinate
                armorStand.teleport(armorStandLocation.add(0, 0.1, 0));
            }


            if (isArmorStandBuffed>0){
                if (target == null) {
                // No mobs found, move toward the player
                Location playerLocation = player.getLocation();
                if (armorStandLocation.distance(playerLocation) > 2) {
                    Vector direction = playerLocation.toVector().subtract(armorStandLocation.toVector()).normalize();
                    armorStand.teleport(armorStandLocation.add(direction.multiply(0.4)));
                }
            } else {

                    Location targetLocation = target.getLocation();
                    Vector direction = targetLocation.toVector().subtract(armorStandLocation.toVector()).normalize();
                    armorStand.teleport(armorStandLocation.add(direction.multiply(0.6)));


                    if (armorStandLocation.distance(targetLocation) <= 2) {

                        List<LivingEntity> nearbyEntities = getNearbyLivingEntitiesArmorStand(armorStand, 2);

                        for (LivingEntity entity : nearbyEntities) {
                            entity.damage(16.0);
                        }
                    }
                }
                armorStand.getWorld().spawnParticle(
                    Particle.DUST,
                    armorStand.getLocation().add(0, 1, 0), // Adjust Y-axis for head height
                    10, // Number of particles
                    0.2, 0.5, 0.2, // Spread in X, Y, Z
                    new Particle.DustOptions(Color.BLACK, 1.0f) // Blood red color
                );
                isArmorStandBuffed--;
            }else{
                if (target == null) {
                // No mobs found, move toward the player
                Location playerLocation = player.getLocation();
                if (armorStandLocation.distance(playerLocation) > 1.5) {
                    Vector direction = playerLocation.toVector().subtract(armorStandLocation.toVector()).normalize();
                    armorStand.teleport(armorStandLocation.add(direction.multiply(0.2)));
                }
            } else {
                    // Move toward the target mob
                    Location targetLocation = target.getLocation();
                    Vector direction = targetLocation.toVector().subtract(armorStandLocation.toVector()).normalize();
                    armorStand.teleport(armorStandLocation.add(direction.multiply(0.2)));

                    // Damage the target if within 1 block
                    if (armorStandLocation.distance(targetLocation) <= 1) {
                        target.damage(5.0); // Deal 5 hearts of damage
                    }
                }
            }
            lifetime++;
        }
    }.runTaskTimer(plugin, 0L, 1L); // Run every tick
}


    private LivingEntity findNearestMob(ArmorStand armorStand) {
        World world = armorStand.getWorld();
        LivingEntity nearest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : world.getNearbyEntities(armorStand.getLocation(), 16, 16, 16)) {
            if (entity instanceof LivingEntity && !(entity instanceof ArmorStand) && !(entity instanceof Player)) {
                double distance = armorStand.getLocation().distance(entity.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    nearest = (LivingEntity) entity;
                }
            }
        }
        return nearest;
    }
    private List<LivingEntity> getNearbyLivingEntitiesArmorStand(ArmorStand armorStand, double radius) {
    List<LivingEntity> livingEntities = new ArrayList<>();
    World world = armorStand.getWorld();

    // Get all entities within the radius
    for (Entity entity : world.getNearbyEntities(armorStand.getLocation(), radius, radius, radius)) {
        if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
            livingEntities.add((LivingEntity) entity);
        }
    }

    return livingEntities;
    }
    private List<LivingEntity> getNearbyLivingEntitiesAroundPlayer(Entity player, double radius) {
    List<LivingEntity> livingEntities = new ArrayList<>();
    World world = player.getWorld();

    // Get all entities within the radius
    for (Entity entity : world.getNearbyEntities(player.getLocation(), radius, radius, radius)) {
        if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
            livingEntities.add((LivingEntity) entity);
        }
    }

    return livingEntities;
    }


}
