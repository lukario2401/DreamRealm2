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
    if (isAnimationRunning) {
        return;
    }
    isAnimationRunning = true;

    new BukkitRunnable() {
        double angle = 0; // Initialize rotation angle

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isHoldingTheCorrectItem(player)) {
                    Location playerLocation = player.getEyeLocation();
                    Vector headDirection = playerLocation.getDirection().normalize();

                    double handLength = 2.0; // Length of the clock hand
                    double handHeight = handLength; // Set equal to handLength for consistent radius

                    double backOffset = -1.0;
                    double upOffset = 1.5;

                    Vector flatDirection = new Vector(headDirection.getX(), 0, headDirection.getZ()).normalize();
                    Vector perpendicular = flatDirection.clone().rotateAroundY(Math.PI / 2);

                    double centerX = playerLocation.getX() + flatDirection.getX() * backOffset;
                    double centerY = playerLocation.getY() + upOffset;
                    double centerZ = playerLocation.getZ() + flatDirection.getZ() * backOffset;

                    // Calculate rotating point with consistent radius
                    double rotatingX = centerX + perpendicular.getX() * handLength * Math.cos(Math.toRadians(angle));
                    double rotatingY = centerY + handHeight * Math.sin(Math.toRadians(angle)); // Now uses handHeight = handLength
                    double rotatingZ = centerZ + perpendicular.getZ() * handLength * Math.cos(Math.toRadians(angle));

                    // Draw particles along the path
                    for (double t = 0; t <= 1; t += 0.1) {
                        double x = centerX + t * (rotatingX - centerX);
                        double y = centerY + t * (rotatingY - centerY);
                        double z = centerZ + t * (rotatingZ - centerZ);

                        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }
            angle += 2;
        }
    }.runTaskTimer(plugin, 0L, 1L);
}

boolean isAnimationRunningg = false;
@EventHandler
public void startFlameAnimationn(PlayerItemHeldEvent event) {
    if (isAnimationRunningg) {
        return;
    }
    isAnimationRunningg = true;

    new BukkitRunnable() {
        double angle = 0; // Initialize rotation angle

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isHoldingTheCorrectItem(player)) {
                    Location playerLocation = player.getEyeLocation();
                    Vector headDirection = playerLocation.getDirection().normalize();

                    double handLength = 1.25; // Length of the clock hand
                    double handHeight = handLength; // Set equal to handLength for consistent radius

                    double backOffset = -1.0;
                    double upOffset = 1.5;

                    Vector flatDirection = new Vector(headDirection.getX(), 0, headDirection.getZ()).normalize();
                    Vector perpendicular = flatDirection.clone().rotateAroundY(Math.PI / 2);

                    double centerX = playerLocation.getX() + flatDirection.getX() * backOffset;
                    double centerY = playerLocation.getY() + upOffset;
                    double centerZ = playerLocation.getZ() + flatDirection.getZ() * backOffset;

                    // Calculate rotating point with consistent radius
                    double rotatingX = centerX + perpendicular.getX() * handLength * Math.cos(Math.toRadians(angle));
                    double rotatingY = centerY + handHeight * Math.sin(Math.toRadians(angle)); // Now uses handHeight = handLength
                    double rotatingZ = centerZ + perpendicular.getZ() * handLength * Math.cos(Math.toRadians(angle));

                    // Draw particles along the path
                    for (double t = 0; t <= 1; t += 0.1) {
                        double x = centerX + t * (rotatingX - centerX);
                        double y = centerY + t * (rotatingY - centerY);
                        double z = centerZ + t * (rotatingZ - centerZ);

                        player.getWorld().spawnParticle(Particle.FLAME, x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }
            angle += 0.2;
        }
    }.runTaskTimer(plugin, 0L, 1L);
}

    boolean isAnimationRunninggg = false;
    @EventHandler
    public void startFlamingAnimation(PlayerItemHeldEvent event){

        if(isAnimationRunninggg){return;}
        isAnimationRunninggg=true;

        startFlameAnimationCircle(0);
        startFlameAnimationCircle(22);
        startFlameAnimationCircle(45);
        startFlameAnimationCircle(67);
        startFlameAnimationCircle(90);
        startFlameAnimationCircle(115);
        startFlameAnimationCircle(135);
        startFlameAnimationCircle(155);
        startFlameAnimationCircle(180);
        startFlameAnimationCircle(205);
        startFlameAnimationCircle(225);
        startFlameAnimationCircle(245);
        startFlameAnimationCircle(270);
        startFlameAnimationCircle(290);
        startFlameAnimationCircle(315);
        startFlameAnimationCircle(335);

    }


    public void startFlameAnimationCircle(double sine) {
    new BukkitRunnable() {
        double angle = sine; // Initialize rotation angle
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isHoldingTheCorrectItem(player)) {
                                        Location playerLocation = player.getEyeLocation();
                    Vector headDirection = playerLocation.getDirection().normalize();

                    double handLength = 2.5; // Length of the clock hand
                    double handHeight = handLength; // Set equal to handLength for consistent radius

                    double backOffset = -1.0;
                    double upOffset = 1.5;

                    Vector flatDirection = new Vector(headDirection.getX(), 0, headDirection.getZ()).normalize();
                    Vector perpendicular = flatDirection.clone().rotateAroundY(Math.PI / 2);

                    double centerX = playerLocation.getX() + flatDirection.getX() * backOffset;
                    double centerY = playerLocation.getY() + upOffset;
                    double centerZ = playerLocation.getZ() + flatDirection.getZ() * backOffset;

                    // Calculate rotating point with consistent radius
                    double rotatingX = centerX + perpendicular.getX() * handLength * Math.cos(Math.toRadians(angle));
                    double rotatingY = centerY + handHeight * Math.sin(Math.toRadians(angle)); // Now uses handHeight = handLength
                    double rotatingZ = centerZ + perpendicular.getZ() * handLength * Math.cos(Math.toRadians(angle));

                    // Draw particles along the path
                    for (double t = 1.0; t <= 1; t += 0.1) {
                        double x = centerX + t * (rotatingX - centerX);
                        double y = centerY + t * (rotatingY - centerY);
                        double z = centerZ + t * (rotatingZ - centerZ);

                        player.getWorld().spawnParticle(Particle.SOUL, x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }
            angle += 2;
        }
    }.runTaskTimer(plugin, 0L, 1L); // Run every tick (20 times per second)
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