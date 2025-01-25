package me.lukario.dreamRealm2.items.ranged.misc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Cards implements Listener {
    private final Plugin plugin;

    public Cards(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#e668c6") + "Cards";//D88F07
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Fate";
    private static final Material ITEM_MATERIAL = Material.PAPER;

    public static ItemStack createItem(int customModelDataID) {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(customModelDataID);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void cardPlay(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (!isHoldingTheCorrectItem(player)){return;}
        if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()== Action.LEFT_CLICK_BLOCK){

            int randomNumber = ThreadLocalRandom.current().nextInt(10, 14);

            if (player.getCooldown(createItem(randomNumber))==0){

                player.getInventory().setItemInMainHand(createItem(randomNumber));
                player.setCooldown(createItem(randomNumber),10);

            }
        }
        if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()== Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            int customModelData = 0;
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasCustomModelData()) {
                    // Retrieve the custom model data
                    customModelData = meta.getCustomModelData();
                }
            }

            if (customModelData == 10) {

                aceAbility(player);
            }
            if (customModelData == 11) {

                blackAceAbility(player);
            }
            if (customModelData == 12) {

                redJokerAbility(player);
            }
            if (customModelData == 13) {

                blackJokerAbility(player);
            }

        }

    }


    public void redJokerAbility(Player player) {
    int radius = 10; // Radius of effect
    player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).forEach(entity -> {
        if (entity instanceof LivingEntity && !entity.equals(player)) {
            Location randomLocation = player.getLocation().add(
                (Math.random() - 0.5) * radius * 2,
                (Math.random() * 5) - 2.5,
                (Math.random() - 0.5) * radius * 2
            );
            entity.teleport(randomLocation);
            player.getWorld().spawnParticle(Particle.PORTAL, randomLocation, 50, 0.5, 0.5, 0.5, 0.1);
        }
    });
    }
    public void blackJokerAbility(Player player) {
        LivingEntity nearestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : player.getWorld().getNearbyLivingEntities(player.getLocation(), 10)) {
            if (!entity.equals(player) && entity instanceof LivingEntity) {
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    nearestEntity = entity;
                }
            }
        }

        if (nearestEntity != null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 1));
            nearestEntity.addPassenger(player);

            LivingEntity finalNearestEntity = nearestEntity;
            new BukkitRunnable() {
                @Override
                public void run() {
                    finalNearestEntity.removePassenger(player);
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);

                }
            }.runTaskLater(plugin, 600L);
        }
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
    public void blackAceAbility(Player player) {
    LivingEntity target = null;
    double closestDistance = Double.MAX_VALUE;

    for (LivingEntity entity : player.getWorld().getNearbyLivingEntities(player.getLocation(), 20)) {
        if (!entity.equals(player) && entity instanceof LivingEntity) {
            double distance = player.getLocation().distance(entity.getLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                target = entity;
            }
        }
    }

    if (target != null) {
        Location behindTarget = target.getLocation().clone().add(
            -Math.sin(Math.toRadians(target.getLocation().getYaw())) * 2,
            0,
            Math.cos(Math.toRadians(target.getLocation().getYaw())) * 2
        );
        player.teleport(behindTarget);
        target.damage(10, player);

        player.getWorld().spawnParticle(Particle.SMOKE, target.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
    }
    }
    public void aceAbility(Player player) {
    int radius = 10; // Radius of effect
    player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).forEach(entity -> {
        if (entity instanceof Player) {
            Player ally = (Player) entity;

            // Add healing and absorption effects
            ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
            ally.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 2));

            // Remove negative effects manually
            ally.getActivePotionEffects().stream()
                .filter(effect -> isNegativeEffect(effect.getType()))
                .forEach(effect -> ally.removePotionEffect(effect.getType()));

            // Send feedback and show particles
            ally.sendMessage("The Ace of Hearts has healed you!");
            ally.getWorld().spawnParticle(Particle.HEART, ally.getLocation(), 20, 0.5, 1, 0.5, 0.1);
        }
    });
}

    private boolean isNegativeEffect(PotionEffectType type) {
        return type == PotionEffectType.BLINDNESS ||
               type == PotionEffectType.HUNGER ||
               type == PotionEffectType.POISON ||
               type == PotionEffectType.SLOWNESS ||
               type == PotionEffectType.WEAKNESS ||
               type == PotionEffectType.WITHER ||
               type == PotionEffectType.UNLUCK;
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
