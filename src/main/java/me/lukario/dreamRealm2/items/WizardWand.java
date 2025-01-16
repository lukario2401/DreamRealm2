package me.lukario.dreamRealm2.items;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class WizardWand implements Listener {

    private final Plugin plugin;

    // Constructor to accept plugin instance
    public WizardWand(Plugin plugin) {
        this.plugin = plugin;
    }

    // Create the custom wand
    public static ItemStack createCustomWizardWand() {
        ItemStack wizardWand = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = wizardWand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Wizard Wand");
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW + "Right-click and Left-click for lightning strike, don't forget to sneak"
            ));
            wizardWand.setItemMeta(meta);
        }
        return wizardWand;
    }

    // Assign metadata to the player (Persistent)
    public void setPlayerMetadata(Player player, String tag) {
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, tag), PersistentDataType.BYTE, (byte) 1);
    }

    // Check if player has specific metadata
    public boolean hasPlayerMetadata(Player player, String tag) {
        return player.getPersistentDataContainer().has(new NamespacedKey(plugin, tag), PersistentDataType.BYTE);
    }

    // Remove metadata from player
    public void removePlayerMetadata(Player player, String tag) {
        player.getPersistentDataContainer().remove(new NamespacedKey(plugin, tag));
    }

    // Event handler when a player interacts (Right-click or Left-click)
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Get the item in the player's main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() ||
                !meta.getDisplayName().equals(ChatColor.GOLD + "Wizard Wand")) {
            return;
        }

        // Handle metadata assignment based on actions
        boolean isSneaking = player.isSneaking();
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                setPlayerMetadata(player, "fl");
                setPlayerMetadata(player, isSneaking ? "sl" : "sr");
//                player.sendMessage(ChatColor.DARK_BLUE + "You have received the 'fl' and '" +
//                        (isSneaking ? "sl" : "sr") + "' metadata!");
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                setPlayerMetadata(player, "fr");
                setPlayerMetadata(player, isSneaking ? "sr" : "sl");
//                player.sendMessage(ChatColor.AQUA + "You have received the 'fr' and '" +
//                        (isSneaking ? "sr" : "sl") + "' metadata!");
                break;
        }

        // Trigger abilities
        if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sl")) {
            teleport(player);
            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sl");
        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sr")) {
            buff(player);
            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sr");
        }
        if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sl")) {
            triggerIceLaunch(player);
            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sl");
        }
        if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sr")) {
            triggerLightningStrike(player);
            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sr");
        }
    }

    private void buff(Player player){

        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, 200, 1); // SPEED for 10 seconds (200 ticks) at level 2
        PotionEffect potionEffectt = new PotionEffect(PotionEffectType.REGENERATION, 200, 1); // SPEED for 10 seconds (200 ticks) at level 2
        PotionEffect potionEffecttt = new PotionEffect(PotionEffectType.STRENGTH, 200, 1); // SPEED for 10 seconds (200 ticks) at level 2

        // Add the potion effect to the player
        player.addPotionEffect(potionEffect);
        player.addPotionEffect(potionEffectt);
        player.addPotionEffect(potionEffecttt);

        // Optional: Notify the player
//        player.sendMessage("You have been given a speed boost!");
    }

    private void teleport(Player player){
            // Get the player's eye location (the direction they are looking)
    Location eyeLocation = player.getEyeLocation();
    // Get the direction vector the player is looking at
    Vector direction = eyeLocation.getDirection();
    // Scale the direction vector to the desired distance
    Vector scaledDirection = direction.multiply(10);
    // Add the scaled direction vector to the player's current location
    Location teleportLocation = eyeLocation.add(scaledDirection);
    // Teleport the player to the new location
    player.teleport(teleportLocation);
    // Optionally play a sound or send a message to indicate the teleportation
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
//    player.sendMessage(ChatColor.AQUA + "You teleported in the direction you were looking!");
    }



    private void triggerLightningStrike(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        RayTraceResult rayTraceResult = player.getWorld().rayTraceBlocks(eyeLocation, direction, 100);
        Location targetLocation = (rayTraceResult != null && rayTraceResult.getHitBlock() != null)
                ? rayTraceResult.getHitBlock().getLocation()
                : eyeLocation.add(direction.multiply(100));

        player.getWorld().strikeLightning(targetLocation);
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
    }

private void triggerIceLaunch(Player player) {
    Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection();

    // Only calculate the location for the second ice block
    Location spawnLocation = eyeLocation.add(direction.multiply(2)); // Adjust spawn distance as needed

    // Spawn and launch the second ice block as a falling block
    player.getWorld().spawn(spawnLocation, FallingBlock.class, fallingBlock -> {
        fallingBlock.setDropItem(false); // Prevent the block from dropping
        fallingBlock.setBlockData(Material.ICE.createBlockData()); // Set the ice block data
        fallingBlock.setVelocity(direction.multiply(1.5)); // Apply the velocity to launch the ice block
    });

    // Optionally, you can play a sound to indicate that the ice block has been launched
    player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
}
    @EventHandler
    public void onIceBlockLand(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            if (fallingBlock.getBlockData().getMaterial() == Material.ICE) {
                Location iceBlockLocation = event.getBlock().getLocation();
                fallingBlock.getWorld().spawnParticle(
                    Particle.FIREWORK,  // Particle type
                    fallingBlock.getLocation().add(0, 0.5, 0), // 3 blocks above the player
                    50,                        // Number of particles
                    0.5, 0.5, 0.5,             // Slight spread in X, Y, Z
                    0.2                          // Speed (particles don’t move)
                );

                // Iterate over nearby entities directly (Collection<Entity>)
                for (Entity entity : iceBlockLocation.getWorld().getNearbyEntities(iceBlockLocation, 5, 5, 5)) {
                    if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
                        ((LivingEntity) entity).damage(11.5);
                    }
                }

                // Remove the ice block after landing
                event.getBlock().setType(Material.AIR);
            }
        }
    }
}
