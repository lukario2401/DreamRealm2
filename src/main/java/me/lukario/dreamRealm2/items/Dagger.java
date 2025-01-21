package me.lukario.dreamRealm2.items;

import com.comphenix.protocol.PacketType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class Dagger implements Listener {

    private final Plugin plugin;

    public Dagger(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = net.md_5.bungee.api.ChatColor.of("#D88F07") + "Dagger";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating king";
    private static final Material ITEM_MATERIAL = Material.NETHERITE_SWORD;

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
    public void dagger(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        final boolean[] canShoot = {true};

        if (!isHoldingTheCorrectItem(player)) {
            return;
        }
        if(!canShoot[0]){
            player.sendMessage("wait for the dagger to come back");
            return;
        }
        if (Action.RIGHT_CLICK_AIR == event.getAction() || Action.RIGHT_CLICK_BLOCK == event.getAction()) {
            canShoot[0] = false;

            Location spawnLocation = player.getLocation().add(0, 1, 0);
            ArmorStand armorStand = spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class);

            ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(10);
            item.setItemMeta(meta);

            armorStand.setItem(EquipmentSlot.HEAD,item);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);


//            armorStandLocation.setDirection(playerDirection);
//            armorStand.teleport(armorStandLocation);
//
//            Vector velocity = playerDirection.multiply(2);
//            armorStand.setVelocity(velocity);
            final boolean[] outOfBounds = {false};
            int[] armorStandAliveTime = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!armorStand.isValid()) {
                        this.cancel();
                        return;
                    }

                    armorStandAliveTime[0]++;

                    Vector armorStandDirection = armorStand.getLocation().getDirection();
                    Location playerLocation = player.getLocation();
                    Location armorStandLocation = armorStand.getLocation();

                    double distance = playerLocation.distance(armorStandLocation);

                    if (distance < 16 && !outOfBounds[0]) {
                        Vector direction = armorStandDirection.clone().normalize().multiply(1);
                        armorStandLocation.add(direction);
                        armorStand.teleport(armorStandLocation);
                        armorStand.getWorld().spawnParticle(Particle.SNOWFLAKE,armorStandLocation,1);

                        for(LivingEntity livingEntity : armorStandLocation.getNearbyLivingEntities(2)){
                            if (!livingEntity.equals(player)){
                                livingEntity.damage(12,player);
                            }
                        }

                    } else {
                        outOfBounds[0] = true;

                        Vector direction = player.getLocation().toVector().subtract(armorStandLocation.toVector()).normalize();
                        // Move the armor stand in the direction
                        armorStandLocation.add(direction.multiply(1)); // Adjust the multiplier for desired movement speed
                        // Update the armor stand's direction to face the player
                        armorStandLocation.setDirection(direction);
                        // Teleport the armor stand to the new location
                        armorStand.teleport(armorStandLocation);
                        armorStand.getWorld().spawnParticle(Particle.SNOWFLAKE,armorStandLocation,1);

                        for(LivingEntity livingEntity : armorStandLocation.getNearbyLivingEntities(2)){
                            if (!livingEntity.equals(player)){
                                livingEntity.damage(21,player);
                            }
                        }
                    }

                    if (armorStandAliveTime[0] > 10) {
                        if (distance <= 0.5) {
                            canShoot[0] = true;
                            armorStand.remove();
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
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
