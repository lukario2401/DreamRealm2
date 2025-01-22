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

    private double angle = 0;

    public void startFlameAnimation(PlayerItemHeldEvent event) {
    // Schedule a repeating task to create a rotating flame for players holding the clock
    new BukkitRunnable() {
        @Override
            public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isHoldingTheCorrectItem(player)) {
                    Location playerLocation = player.getLocation();

                    double handLength = 3.0; // Length of the clock hand
                    double handHeightOffset = 1.0; // Height of the clock hand relative to the player's feet

                    // Get the center point at the player's feet
                    double centerX = playerLocation.getX();
                    double centerZ = playerLocation.getZ();
                    double centerY = playerLocation.getY() + handHeightOffset;

                    // Calculate the end point of the clock hand
                    double endX = centerX + handLength * Math.cos(angle);
                    double endZ = centerZ + handLength * Math.sin(angle);
                    double endY = centerY; // Keep the hand flat (horizontal)

                    // Draw particles along the clock hand
                    for (double t = 0; t <= 1; t += 0.1) { // Divide the hand into 10 segments
                        double x = centerX + t * (endX - centerX);
                        double y = centerY + t * (endY - centerY);
                        double z = centerZ + t * (endZ - centerZ);

                        // Spawn a particle at this segment
                        player.getWorld().spawnParticle(Particle.SOUL, x, y, z, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, x, y, z, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.SCULK_SOUL, x, y, z, 1, 0, 0, 0, 0);
                    }
                }
            }

            // Increment the angle for the next frame
            angle += Math.PI / 64; // Rotate by 1/64th of a circle for smooth animation
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