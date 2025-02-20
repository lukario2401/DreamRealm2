package me.lukario.dreamRealm2.items.swords.ability;

import me.lukario.dreamRealm2.RayCast;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffectType;


import java.util.Arrays;

public class SinOfSolace implements Listener {

    public static ItemStack createSinOfSolace() {
        ItemStack SinOfSolace = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = SinOfSolace.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_GREEN + "Sin Of Solace");
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW + "This blade slowly drives its wielder mad"
            ));
            SinOfSolace.setItemMeta(meta);
        }
        return SinOfSolace;
    }

    private final Plugin plugin;

    // Constructor to accept plugin instance
    public SinOfSolace(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setPlayerMetadata(Player player, String tag) {
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, tag), PersistentDataType.BYTE, (byte) 1);
    }

    // Check if player has specific metadata
    public boolean hasPlayerMetadata(Player player, String tag) {
        return player.getPersistentDataContainer().has(new NamespacedKey(plugin, tag), PersistentDataType.BYTE);
    }

    public void removePlayerMetadata(Player player, String tag) {
        player.getPersistentDataContainer().remove(new NamespacedKey(plugin, tag));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Get the item in the player's main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() ||
                !meta.getDisplayName().equals(ChatColor.DARK_GREEN + "Sin Of Solace")) {
            return;
        }

        // Handle metadata assignment based on actions
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

        // Trigger abilities and ensure the right metadata combinations
        if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sr")) {
//            player.sendMessage(ChatColor.DARK_GREEN + "Left Click");
            RayCast.raycastWithCritParticle(player, 0.2F, 11.5F, 5F);
            RayCast.raycastWithCritParticle(player, 0.1F, 11.5F, 5F);
            RayCast.raycastWithCritParticle(player, -0.0F, 11.5F, 5F);
            RayCast.raycastWithCritParticle(player, -0.1F, 11.5F, 5F);
            RayCast.raycastWithCritParticle(player, -0.2F, 11.5F, 5F);

            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sr");
        } else if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sl")) {

            dash(event,player);

//            player.sendMessage(ChatColor.DARK_GREEN + "Sneak Left Click");
            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sl");
        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sl")) {


            pushMobToPlayer(player);
//            player.sendMessage(ChatColor.DARK_GREEN + "Right Click");
            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sl");
        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sr")) {
            buff(player);
//            player.sendMessage(ChatColor.DARK_GREEN + "Sneak Right Click");
            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sr");
        }
    }

    private void dash(PlayerInteractEvent event, Player player) {
        // Check if the player right-clicked

            ItemStack item = player.getInventory().getItemInMainHand();

            // Check if the item is the custom sword


                // Launch the player forward
                Vector direction = player.getLocation().getDirection().multiply(4).setY(0.2);
                player.setVelocity(direction);

                // Play a sound effect
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);

                // Deal damage to mobs as the player is dashing forward
                dealDamageToMobsAlongDash(player, direction);


    }

    private void dealDamageToMobsAlongDash(Player player, Vector direction) {
        // Check for mobs in the path of the dash while the player is moving
        final double damageDistance = 1.0; // Distance to check around the player
        final double dashSpeed = 0.5; // How fast the player moves in their dash (set a lower speed for smoother damage detection)

        // Set an interval to check for mobs in front of the player
        for (double i = 0; i <= 4.0; i += dashSpeed) {
            // Calculate the position ahead of the player based on their direction
            Vector position = player.getLocation().toVector().add(direction.clone().multiply(i));
//            player.getWorld().spawnParticle(
//                    Particle.EXPLOSION_EMITTER,  // Particle type
//                    player.getLocation().add(0, 1, 0), // 1 blocks above the player
//                    3,                        // Number of particles
//                    0, 0, 0,             // Slight spread in X, Y, Z
//                    0                          // Speed (particles don’t move)
//                );

            // Check for entities within a small radius (1.0 block) around the position
            for (Entity entity : player.getWorld().getEntities()) {
                // Check if the entity is a living entity and not the player
                if (entity instanceof LivingEntity && entity != player) {
                    LivingEntity livingEntity = (LivingEntity) entity;

                    // Check if the entity is within the range of the path
                    if (entity.getLocation().distance(position.toLocation(player.getWorld())) < damageDistance) {
                        // Deal damage to the mob
                        livingEntity.damage(21.0); // Adjust the damage value as needed
                        player.getWorld().spawnParticle(
                            Particle.SOUL,  // Particle type
                            player.getLocation().add(0, 1.25, 0), // 1 blocks above the player
                            50,                        // Number of particles
                            0.1, 0.1, 0.1,             // Slight spread in X, Y, Z
                            0.15                          // Speed (particles don’t move)
                        );
                        player.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
                    }
                }
            }
        }
    }
    private void buff(Player player){

        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, 600, 1); // SPEED for 10 seconds (200 ticks) at level 2
        PotionEffect potionEffectt = new PotionEffect(PotionEffectType.REGENERATION, 600, 1); // SPEED for 10 seconds (200 ticks) at level 2
        PotionEffect potionEffecttt = new PotionEffect(PotionEffectType.STRENGTH, 600, 1); // SPEED for 10 seconds (200 ticks) at level 2
        PotionEffect potionEffectttt = new PotionEffect(PotionEffectType.INVISIBILITY, 600, 1); // SPEED for 10 seconds (200 ticks) at level 2
        PotionEffect potionEffecttttt = new PotionEffect(PotionEffectType.SATURATION, 600, 1); // SPEED for 10 seconds (200 ticks) at level 2

        // Add the potion effect to the player
        player.addPotionEffect(potionEffect);
        player.addPotionEffect(potionEffectt);
        player.addPotionEffect(potionEffecttt);
        player.addPotionEffect(potionEffectttt);
        player.addPotionEffect(potionEffecttttt);

    }

    public void pushMobToPlayer(Player player) {

        Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection();

            RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(eyeLocation, direction, 12, entity -> entity instanceof LivingEntity && !(entity instanceof Player)); // Max range is 100 blocks

            if (rayTraceResult != null && rayTraceResult.getHitEntity() != null) {
                Entity hitEntity = rayTraceResult.getHitEntity();

                // Check if the hit entity is a mob (not a player)
                if (hitEntity instanceof LivingEntity && !(hitEntity instanceof Player)) {
                    LivingEntity livingEntity = (LivingEntity) hitEntity;

                    // Calculate the direction to push the mob towards the player
                    Vector directionToPlayer = player.getLocation().toVector().subtract(livingEntity.getLocation().toVector()).normalize();

                    // Apply a force to the mob pushing it towards the player
                    livingEntity.setVelocity(directionToPlayer.multiply(1.5)); // Modify the multiplier to change the push force

                    // Optional: Send a message to the player
//                    player.sendMessage("You pushed a mob towards you!");
                }
            }
        }
    }
