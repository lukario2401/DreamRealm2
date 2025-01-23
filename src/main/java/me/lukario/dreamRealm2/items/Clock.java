package me.lukario.dreamRealm2.items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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


      @EventHandler
public void startFlameAnimation(PlayerItemHeldEvent event) {
    // Schedule a repeating task to create a vertical rotating flame for players holding the clock
    new BukkitRunnable() {
        double angle = 0; // Initialize rotation angle

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isHoldingTheCorrectItem(player)) {
                    Location playerLocation = player.getEyeLocation();
                    Vector headDirection = playerLocation.getDirection().normalize(); // Direction the player is looking

                    double handLength = 6.0; // Length of the clock hand
                    double handHeight = 3.0; // Vertical height of the rotation

                    // Project head direction onto the horizontal plane (ignore Y-axis)
                    Vector flatDirection = new Vector(headDirection.getX(), 0, headDirection.getZ()).normalize();

                    // Calculate the perpendicular vector in the horizontal plane
                    Vector perpendicular = flatDirection.clone().rotateAroundY(Math.PI / 2); // Rotate by 90 degrees to get the axis of rotation

                    // Center of rotation (around the player's eyes)
                    double centerX = playerLocation.getX();
                    double centerY = playerLocation.getY();
                    double centerZ = playerLocation.getZ();

                    // Calculate the rotating point in 3D space
                    double rotatingX = centerX + perpendicular.getX() * handLength * Math.cos(angle);
                    double rotatingY = centerY + handHeight * Math.sin(angle); // Use sine for vertical movement
                    double rotatingZ = centerZ + perpendicular.getZ() * handLength * Math.cos(angle);

                    // Draw particles along the rotating path
                    for (double t = 0; t <= 1; t += 0.1) { // Divide the path into 10 segments
                        double x = centerX + t * (rotatingX - centerX);
                        double y = centerY + t * (rotatingY - centerY);
                        double z = centerZ + t * (rotatingZ - centerZ);

                        // Spawn particles at this segment
                        player.getWorld().spawnParticle(Particle.SOUL, x, y, z, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, x, y, z, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.SCULK_SOUL, x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }
            // Increment the angle for the next frame

            angle += Math.PI / 64; // Rotate by a small increment (1/64 of a circle per tick)
            if (angle >= 2 * Math.PI) {
                angle = 0; // Reset angle after a full rotation
            }
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