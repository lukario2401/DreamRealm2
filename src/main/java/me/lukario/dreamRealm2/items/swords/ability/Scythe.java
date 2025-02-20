package me.lukario.dreamRealm2.items.swords.ability;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Objects;

public class Scythe implements Listener {

    private final Plugin plugin;

    public Scythe(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = net.md_5.bungee.api.ChatColor.of("#e668c6") + "Scythe";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Death";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

    public static ItemStack createItem() {

        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(18);
            meta.addEnchant(Enchantment.SHARPNESS,10,true);
            meta.addEnchant(Enchantment.LOOTING,5,true);
            meta.addEnchant(Enchantment.SWEEPING_EDGE,5,true);
            meta.addEnchant(Enchantment.FIRE_ASPECT,5,true);
            item.setItemMeta(meta);

        }
        return item;
    }

    @EventHandler
    public void scythe(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (isHoldingTheCorrectItem(player)){
            if (event.getAction()== Action.RIGHT_CLICK_AIR || event.getAction()== Action.RIGHT_CLICK_BLOCK){

                float distancee = 64;

                Location eyeLocation = player.getEyeLocation();
                Vector direction = eyeLocation.getDirection();

                // Ray trace to detect entities within range
                RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(
                        eyeLocation,
                        direction,
                        distancee,
                        entity -> entity instanceof LivingEntity && !(entity.equals(player))
                );

                if (rayTraceResult != null && rayTraceResult.getHitEntity() != null) {
                    Entity hitEntity = rayTraceResult.getHitEntity();

                    // Check if the hit entity is a LivingEntity
                    if (hitEntity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) hitEntity;

                        startRaycast(player,livingEntity);

                    }
                }

                // Visualize the ray with particles
                for (double i = 0; i <= distancee; i += 0.5) {
                    Location particleLocation = eyeLocation.clone().add(direction.clone().multiply(i));
                    player.getWorld().spawnParticle(Particle.DUST,particleLocation.add(0, 0, 0),10,0, 0, 0,
                        new Particle.DustOptions(Color.RED, 1.0f));
                }

            }
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
    private void startRaycast(Entity entity1, Entity entity2) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Check if both entities are valid
                if (!entity1.isValid() || !entity2.isValid()) {
                    this.cancel();
                    return;
                }

                // Get locations of both entities
                Location start = entity1.getLocation().add(0, entity1.getHeight() / 2, 0); // Center of entity 1
                Location end = entity2.getLocation().add(0, entity2.getHeight() / 2, 0);  // Center of entity 2

                // Calculate direction and distance
                double distance = start.distance(end);
                if (distance > 50) { // Example limit to prevent excessive particles
                    this.cancel();
                    return;
                }
                Vector direction = end.toVector().subtract(start.toVector()).normalize();

                if (entity2 instanceof LivingEntity){
                    LivingEntity livingEntity = (LivingEntity) entity2;

                    livingEntity.setHealth(livingEntity.getHealth()-0.25);
                    livingEntity.playHurtAnimation(2);
                    livingEntity.getWorld().playSound(livingEntity.getLocation(), Objects.requireNonNull(livingEntity.getHurtSound()),1,1);


                }

                // Spawn particles along the ray
                for (double i = 0; i < distance; i += 0.5) { // 0.5 is the particle spacing
                    Location particleLocation = start.clone().add(direction.clone().multiply(i));
                    start.getWorld().spawnParticle(Particle.WITCH, particleLocation, 1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L); // Run every tick
    }
}
