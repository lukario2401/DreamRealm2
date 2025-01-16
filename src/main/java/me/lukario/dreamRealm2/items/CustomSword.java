package me.lukario.dreamRealm2.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class CustomSword implements Listener {

    public static ItemStack createCustomSword() {
        // Create the custom sword
        ItemStack customSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = customSword.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Launch Sword");
            meta.setLore(Arrays.asList(
                ChatColor.YELLOW + "Right-click to launch forward and damage mobs!"
            ));
            customSword.setItemMeta(meta);
        }
        return customSword;
    }

    private boolean hasCustomSword(Player player) {
        // Check if the player has the custom sword in their inventory
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory) {
            if (item != null && item.hasItemMeta() &&
                item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Launch Sword")) {
                return true;
            }
        }
        return false;
    }

//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//
//        // Give the custom sword only if the player doesn't have it
//        if (!hasCustomSword(player)) {
//            player.getInventory().addItem(createCustomSword());
//            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Launch Sword!");
//        }
//    }

//    @EventHandler
//    public void givePlayerSword(PlayerToggleFlightEvent event) {
//        Player player = event.getPlayer();
//
//        if (!hasCustomSword(player)) {
//            player.getInventory().addItem(createCustomSword());
//            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Launch Sword!");
//        }
//    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if the player right-clicked
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            // Check if the item is the custom sword
            if (item != null && item.hasItemMeta() &&
                item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Launch Sword")) {

                // Launch the player forward
                Vector direction = player.getLocation().getDirection().multiply(4).setY(0.2);
                player.setVelocity(direction);

                // Play a sound effect
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);

                // Deal damage to mobs as the player is dashing forward
                dealDamageToMobsAlongDash(player, direction);
            }
        }
    }

    private void dealDamageToMobsAlongDash(Player player, Vector direction) {
        // Check for mobs in the path of the dash while the player is moving
        final double damageDistance = 1.0; // Distance to check around the player
        final double dashSpeed = 0.5; // How fast the player moves in their dash (set a lower speed for smoother damage detection)

        // Set an interval to check for mobs in front of the player
        for (double i = 0; i <= 4.0; i += dashSpeed) {
            // Calculate the position ahead of the player based on their direction
            Vector position = player.getLocation().toVector().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(
                    Particle.EXPLOSION_EMITTER,  // Particle type
                    player.getLocation().add(0, 1, 0), // 1 blocks above the player
                    3,                        // Number of particles
                    0, 0, 0,             // Slight spread in X, Y, Z
                    0                          // Speed (particles don’t move)
                );

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
}
