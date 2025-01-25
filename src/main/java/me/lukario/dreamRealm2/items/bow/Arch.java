package me.lukario.dreamRealm2.items.bow;

import me.lukario.dreamRealm2.RayCast;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.*;


public class Arch implements Listener {
    int attackCount = 1;
    private final Plugin plugin;
    private final Map<Player, Long> cooldownMap = new HashMap<>();
    private final Map<Player, Long> sneakRightClickCooldownMap = new HashMap<>();
    private final Map<Player, Long> leftClickCooldownMap = new HashMap<>();
    private final int COOLDOWN_MILLISECONDS = 100; // 3-second cooldown
    private final int SNEAK_RIGHT_CLICK_COOLDOWN_MS = 200; // 3-second cooldown
    private final int LEFT_CLICK_COOLDOWN_MAP = 300; // 3-second cooldown

    public Arch(Plugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createTerminator() {
        ItemStack Terminator = new ItemStack(Material.NETHERITE_HOE);
        ItemMeta meta = Terminator.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_PURPLE + "Terminator");
            meta.setLore(Arrays.asList(
                    ChatColor.YELLOW + "Reigns destruction upon your enemies"
            ));
            Terminator.setItemMeta(meta);
        }
        return Terminator;
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

        // Get the item in the player's main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() ||
                !meta.getDisplayName().equals(ChatColor.DARK_PURPLE + "Terminator")) {
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
                 // Apply cooldown
                setPlayerMetadata(player, "fr");
                setPlayerMetadata(player, isSneaking ? "sr" : "sl");
                break;
        }

        if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sr")) {

//          player.sendMessage(ChatColor.DARK_GREEN + "Left Click");
            if(checkLeftClickCooldown(player)){
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 1.0f);

                if (attackCount == 1){
                    RayCast.rayCastWithAnyParticle(player,0.0F,12F,8F,"soul_fire_flame");

                    attackCount = 2;
                } else if (attackCount == 2) {
                    RayCast.rayCastWithAnyParticle(player,0.2F,12F,8F,"soul_fire_flame");
                    RayCast.rayCastWithAnyParticle(player,-0.2F,12F,8F,"soul_fire_flame");

                    attackCount = 3;
                } else if (attackCount == 3) {
                    RayCast.rayCastWithAnyParticle(player,0.0F,12F,8F,"soul_fire_flame");
                    RayCast.rayCastWithAnyParticle(player,0.2F,12F,8F,"soul_fire_flame");
                    RayCast.rayCastWithAnyParticle(player,-0.2F,12F,8F,"soul_fire_flame");
                    attackCount = 4;
                } else if (attackCount == 4) {
                    RayCast.rayCastWithAnyParticle(player,0.0F,12F,8F,"soul_fire_flame");
                    RayCast.rayCastWithAnyParticle(player,0.2F,12F,8F,"soul_fire_flame");
                    RayCast.rayCastWithAnyParticle(player,-0.2F,12F,8F,"soul_fire_flame");
                    RayCast.rayCastWithAnyParticle(player,0.4F,12F,8F,"soul_fire_flame");
                    RayCast.rayCastWithAnyParticle(player,-0.4F,12F,8F,"soul_fire_flame");
                    attackCount = 1;
                }
            }

            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sr");

        } else if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sl")) {

//            player.sendMessage(ChatColor.DARK_GREEN + "Sneak Left Click");
            handleSneakLeftClick(player);
            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sl");

        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sl")) {

            if (checkCooldown(player)){
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 2.0f, 1.0f);
            RayCast.rayCastWithAnyParticle(player, 0F, 16F, 48F, "happy_villager");

//            player.sendMessage(ChatColor.DARK_GREEN + "Right Click");

            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sl");
            }
        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sr")) {
            if (checkSneakRightClickCooldown(player)) {

                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2.0f, 1.0f);
                RayCast.rayCastWithAnyPartAndMultMobs(player, 0F, 18F, 24F, PotionEffectType.POISON, "happy_Villager");
//                player.sendMessage(ChatColor.DARK_GREEN + "Sneak Right Click");
                removePlayerMetadata(player, "fr");
                removePlayerMetadata(player, "sr");
            }
        }
    }

    private boolean checkCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        if (cooldownMap.containsKey(player)) {
            long lastUsed = cooldownMap.get(player);
            if ((currentTime - lastUsed) < COOLDOWN_MILLISECONDS) {
                player.sendMessage(ChatColor.RED + "Ability is on cooldown!");
                return false;
            }
        }
        cooldownMap.put(player, currentTime);
        return true;
    }
    private boolean checkSneakRightClickCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        if (sneakRightClickCooldownMap.containsKey(player)) {
            long lastUsed = sneakRightClickCooldownMap.get(player);
            if ((currentTime - lastUsed) < SNEAK_RIGHT_CLICK_COOLDOWN_MS) {
                player.sendMessage(ChatColor.RED + "Sneak Right Click is on cooldown!");
                return false;
            }
        }
        sneakRightClickCooldownMap.put(player, currentTime);
        return true;
    }
    private boolean checkLeftClickCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        if (leftClickCooldownMap.containsKey(player)) {
            long lastUsed = leftClickCooldownMap.get(player);
            if ((currentTime - lastUsed) < LEFT_CLICK_COOLDOWN_MAP) {
                player.sendMessage(ChatColor.RED + "Left Click is on cooldown!");
                return false;
            }
        }
        leftClickCooldownMap.put(player, currentTime);
        return true;
    }
       private void handleSneakLeftClick(Player player) {
    // Launch bedrock block (ArmorStand)
    Location startLocation = player.getEyeLocation();
    Vector direction = startLocation.getDirection().multiply(2); // Adjust speed
    ArmorStand bedrock = spawnBedrock(startLocation, direction);

    // Create a repeating task to attract mobs every 5 ticks
    BukkitRunnable mobAttractionTask = new BukkitRunnable() {
        @Override
        public void run() {
            // If the bedrock ArmorStand still exists, attract mobs
            if (!bedrock.isDead()) {
                attractMobs(bedrock);
            } else {
                // Remove this task if the bedrock ArmorStand has been removed
                cancel();
            }
        }
    };

    // Start the mob attraction task (every 5 ticks)
    mobAttractionTask.runTaskTimer(plugin, 0, 5); // Schedule every 5 ticks

    // Schedule a task to remove the armor stand after 5 seconds (100 ticks)
    new BukkitRunnable() {
        @Override
        public void run() {
            // Remove the armor stand after 5 seconds
            bedrock.remove();
            // Spawn particle effects and play sound when removed
            bedrock.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, bedrock.getLocation(), 20);
            bedrock.getWorld().playSound(bedrock.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0f, 1.0f);
        }
    }.runTaskLater(plugin, 100); // Delay of 100 ticks (5 seconds)
    }


     private ArmorStand spawnBedrock(Location location, Vector velocity) {
        ArmorStand bedrock = location.getWorld().spawn(location, ArmorStand.class);
        bedrock.setInvisible(true);
        bedrock.setInvulnerable(true);
        bedrock.setGravity(true);
        bedrock.setSmall(true);
        bedrock.isMarker();
        bedrock.getEquipment().setHelmet(new ItemStack(Material.BEDROCK));
        bedrock.setVelocity(velocity);
        return bedrock;
    }

    private void attractMobs(ArmorStand bedrock) {
    Location bedrockLocation = bedrock.getLocation();
    double radius = 10; // Attraction radius

    Collection<Entity> entities = bedrock.getWorld().getNearbyEntities(bedrockLocation, radius, radius, radius);
    for (Entity entity : entities) {
        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
            LivingEntity mob = (LivingEntity) entity;
            Vector attraction = bedrockLocation.toVector().subtract(mob.getLocation().toVector()).normalize();
            mob.setVelocity(attraction.multiply(0.5)); // Adjust force
            mob.damage(5);
        }
    }
}
}
