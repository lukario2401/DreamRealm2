package me.lukario.dreamRealm2.items.special;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class Clock implements Listener {
    private final Plugin plugin;

    public Clock(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#e668c6") + "Clock";//D88F07
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Time";
    private static final Material ITEM_MATERIAL = Material.CLOCK;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(10);
            meta.addEnchant(Enchantment.SHARPNESS,10,true);
            meta.addEnchant(Enchantment.LOOTING,5,true);
            meta.addEnchant(Enchantment.SWEEPING_EDGE,5,true);
            meta.addEnchant(Enchantment.FIRE_ASPECT,5,true);
            item.setItemMeta(meta);

        }
        return item;
    }

    @EventHandler
    public void timeStop(PlayerInteractEvent event){
        Player player = event.getPlayer();


        if (!isHoldingTheCorrectItem(player)){return;}
        if (player.hasCooldown(createItem())){
            player.sendMessage("This item is on cooldown!");
            event.setCancelled(true);
            return;
        }
        if (Action.RIGHT_CLICK_AIR==event.getAction() || Action.RIGHT_CLICK_BLOCK==event.getAction()){

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tick freeze");
            player.setCooldown(createItem(), 100);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tick unfreeze");
                }
            }.runTaskLater(plugin, 100L);

            for (LivingEntity livingEntity : player.getLocation().getNearbyLivingEntities(24)){
                if (!livingEntity.equals(player)){

                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,100,255));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,100,255));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,100,255));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,100,255));

                }
            }
        }
    }

    boolean isAnimationRunning = false;
    @EventHandler
    public void startFlameAnimation(PlayerItemHeldEvent event) {
        if(isAnimationRunning==true){return;}
        isAnimationRunning=true;
    new BukkitRunnable() {
        double angle = 0; // Initialize rotation angle
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isHoldingTheCorrectItem(player)) {
                    Location playerLocation = player.getEyeLocation();
                    Vector headDirection = playerLocation.getDirection().normalize(); // Direction the player is looking

                    double handLength = 3.0; // Length of the clock hand
                    double handHeight = 2.0; // Vertical height of the rotation

                    // Define offsets to move the effect back and up
                    double backOffset = -1.0; // Move particles backward along the player's view direction
                    double upOffset = 1.5;   // Move particles upward from the player's eye level

                    // Project head direction onto the horizontal plane (ignore Y-axis)
                    Vector flatDirection = new Vector(headDirection.getX(), 0, headDirection.getZ()).normalize();

                    // Calculate the perpendicular vector in the horizontal plane
                    Vector perpendicular = flatDirection.clone().rotateAroundY(Math.PI / 2); // Rotate by 90 degrees to get the axis of rotation

                    // Center of rotation (adjusted for back and up offsets)
                    double centerX = playerLocation.getX() + flatDirection.getX() * backOffset;
                    double centerY = playerLocation.getY() + upOffset;
                    double centerZ = playerLocation.getZ() + flatDirection.getZ() * backOffset;

                    // Calculate the rotating point in 3D space
                    double rotatingX = centerX + perpendicular.getX() * handLength * Math.cos(Math.toRadians(angle));
                    double rotatingY = centerY + handHeight * Math.sin(Math.toRadians(angle)); // Use sine for vertical movement
                    double rotatingZ = centerZ + perpendicular.getZ() * handLength * Math.cos(Math.toRadians(angle));

                    // Draw particles along the rotating path
                    for (double t = 0; t <= 1; t += 0.1) { // Divide the path into 10 segments
                        double x = centerX + t * (rotatingX - centerX);
                        double y = centerY + t * (rotatingY - centerY);
                        double z = centerZ + t * (rotatingZ - centerZ);

                        // Spawn particles at this segment
//                        player.getWorld().spawnParticle(Particle.SOUL, x, y, z, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, x, y, z, 1, 0, 0, 0, 0);
//                        player.getWorld().spawnParticle(Particle.SCULK_SOUL, x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }
            angle +=4;
            // Increment the angle for the next frame
//            angle = Math.PI / 64; // Rotate by a small increment (1/64 of a circle per tick)
//            if (angle >= 2 * Math.PI) {
//                angle = 0; // Reset angle after a full rotation
//            }
        }
    }.runTaskTimer(plugin, 0L, 1L); // Run every tick (20 times per second)
}
    boolean isAnimationRunningg = false;
    @EventHandler
    public void startFlameAnimationn(PlayerItemHeldEvent event) {
        if(isAnimationRunningg==true){return;}
        isAnimationRunningg=true;
    new BukkitRunnable() {
        double angle = 0; // Initialize rotation angle
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isHoldingTheCorrectItem(player)) {
                    Location playerLocation = player.getEyeLocation();
                    Vector headDirection = playerLocation.getDirection().normalize(); // Direction the player is looking

                    double handLength = 3.0; // Length of the clock hand
                    double handHeight = 2.0; // Vertical height of the rotation

                    // Define offsets to move the effect back and up
                    double backOffset = -1.0; // Move particles backward along the player's view direction
                    double upOffset = 1.5;   // Move particles upward from the player's eye level

                    // Project head direction onto the horizontal plane (ignore Y-axis)
                    Vector flatDirection = new Vector(headDirection.getX(), 0, headDirection.getZ()).normalize();

                    // Calculate the perpendicular vector in the horizontal plane
                    Vector perpendicular = flatDirection.clone().rotateAroundY(Math.PI / 2); // Rotate by 90 degrees to get the axis of rotation

                    // Center of rotation (adjusted for back and up offsets)
                    double centerX = playerLocation.getX() + flatDirection.getX() * backOffset;
                    double centerY = playerLocation.getY() + upOffset;
                    double centerZ = playerLocation.getZ() + flatDirection.getZ() * backOffset;

                    // Calculate the rotating point in 3D space
                    double rotatingX = centerX + perpendicular.getX() * handLength * Math.cos(Math.toRadians(angle));
                    double rotatingY = centerY + handHeight * Math.sin(Math.toRadians(angle)); // Use sine for vertical movement
                    double rotatingZ = centerZ + perpendicular.getZ() * handLength * Math.cos(Math.toRadians(angle));

                    // Draw particles along the rotating path
                    for (double t = 0; t <= 1; t += 0.1) { // Divide the path into 10 segments
                        double x = centerX + t * (rotatingX - centerX);
                        double y = centerY + t * (rotatingY - centerY);
                        double z = centerZ + t * (rotatingZ - centerZ);

                        // Spawn particles at this segment
//                        player.getWorld().spawnParticle(Particle.SOUL, x, y, z, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.FLAME, x, y, z, 1, 0, 0, 0, 0);
//                        player.getWorld().spawnParticle(Particle.SCULK_SOUL, x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }
            angle +=0.25;
            // Increment the angle for the next frame
//            angle = Math.PI / 64; // Rotate by a small increment (1/64 of a circle per tick)
//            if (angle >= 2 * Math.PI) {
//                angle = 0; // Reset angle after a full rotation
//            }
        }
    }.runTaskTimer(plugin, 0L, 1L); // Run every tick (20 times per second)
}



boolean isAnimationRunningggg = false;

@EventHandler
public void startFlameAnimationnnn(PlayerItemHeldEvent event) {
    if (isAnimationRunningggg) {
        return;
    }
    isAnimationRunningggg = true;

    new BukkitRunnable() {
        double angle = 0;

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isHoldingTheCorrectItem(player)) {
                    Location playerLocation = player.getEyeLocation();
                    Vector headDirection = playerLocation.getDirection().normalize();
                    double radius = 3.2; // Radius of the circle

                    // Circle center offset relative to the player
                    double centerX = playerLocation.getX() + headDirection.getX() * -1.0;
                    double centerY = playerLocation.getY() + 1.5;
                    double centerZ = playerLocation.getZ() + headDirection.getZ() * -1.0;

                    // Calculate perpendicular vectors to define the rotation plane
                    Vector horizontalDirection = new Vector(headDirection.getX(), 0, headDirection.getZ()).normalize();
                    Vector verticalDirection = new Vector(0, 1, 0);

                    // **Change here: Calculate rotation axis for vertical circle**
                    Vector rotationAxis1 = verticalDirection.clone(); // Use vertical direction as the primary axis
                    Vector rotationAxis2 = rotationAxis1.clone().crossProduct(horizontalDirection).normalize();

                    // Spawn particles to create a rotating circle
                    for (int i = 0; i < 4; i++) {
                        double pointAngle = angle + i * (Math.PI / 2); // Offset each point by 90 degrees
                        double offsetX = radius * (rotationAxis1.getX() * Math.cos(pointAngle) + rotationAxis2.getX() * Math.sin(pointAngle));
                        double offsetY = radius * (rotationAxis1.getY() * Math.cos(pointAngle) + rotationAxis2.getY() * Math.sin(pointAngle));
                        double offsetZ = radius * (rotationAxis1.getZ() * Math.cos(pointAngle) + rotationAxis2.getZ() * Math.sin(pointAngle));

                        // Calculate particle position
                        double x = centerX + offsetX;
                        double y = centerY + offsetY;
                        double z = centerZ + offsetZ;

                        // Spawn the particle
                        player.getWorld().spawnParticle(Particle.SOUL, x, y, z, 1, 0, 0, 0, 0);
                    }

                    // Increment angle for rotation
                    angle += 0.1; // Adjust the speed of rotation
                    if (angle > Math.PI * 2) {
                        angle = 0;
                    }
                }
            }
        }
    }.runTaskTimer(plugin, 0L, 1L);
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