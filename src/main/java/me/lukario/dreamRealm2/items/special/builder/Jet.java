package me.lukario.dreamRealm2.items.special.builder;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.eclipse.aether.metadata.Metadata;

import java.util.Arrays;

public class Jet implements Listener {

    private final Plugin plugin;

    public Jet(Plugin plugin) {
        this.plugin = plugin;
    }

     private static final String ITEM_NAME = ChatColor.of("#D88F07") + "AirStrike";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ####";
    private static final Material ITEM_MATERIAL = Material.BLAZE_ROD;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void JetUsedForAirStrike(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (!isHoldingTheCorrectItem(player)){
            return;
        }

        if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()== Action.RIGHT_CLICK_BLOCK){
            LivingEntity livingEntityForAirStrike = getAirStrikeLocationForJetLivingEntity(player);
            if (livingEntityForAirStrike!=null){
                airStrikeAtLocation(livingEntityForAirStrike);
            }else{
                player.sendMessage("Invalid Target");
            }

        }
    }

    private Location getAirStrikeLocationForJet(Player player){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        Location current = null;
        for (float i = 0; i <= 64; i+=0.5f){
            current = location.clone().add(direction.clone().multiply(i));
            current.getWorld().spawnParticle(Particle.CRIT,current,1,0,0,0,0);

            if (current.getBlock().getType()!=Material.AIR){
                return current;
            }

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                if (!livingEntity.equals(player)){
                    if (livingEntity instanceof ArmorStand){}else{
                        return livingEntity.getLocation().setDirection(direction);
                    }
                }
            }
        }
        return current;
    }

    private LivingEntity getAirStrikeLocationForJetLivingEntity(Player player){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        Location current = null;
        LivingEntity livingEntityToReturn = null;

        for (float i = 0; i <= 64; i+=0.5f){
            current = location.clone().add(direction.clone().multiply(i));
            current.getWorld().spawnParticle(Particle.CRIT,current,1,0,0,0,0);

            if (current.getBlock().getType()!=Material.AIR){
                return livingEntityToReturn;
            }

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(2)){
                if (!livingEntity.equals(player)){
                    if (livingEntity instanceof ArmorStand){}else{
                        return livingEntity;
                    }
                }
            }
        }
        return livingEntityToReturn;
    }

    private void airStrikeAtLocation(LivingEntity livingEntity){
        Location location = livingEntity.getLocation();
        location.setPitch(0);

        Location backLocation = location.clone().add(location.getDirection().normalize().clone().multiply(-60));
        backLocation.add(0,30,0);

        ArmorStand armorStand = backLocation.getWorld().spawn(backLocation, ArmorStand.class);

        armorStand.setGravity(false);
        armorStand.setMarker(true);
        armorStand.setInvisible(true);

        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(3);
        item.setItemMeta(meta);

        armorStand.setItem(EquipmentSlot.HAND,item);
        armorStand.getEquipment().setItemInMainHand(item);
        armorStand.setRightArmPose(new EulerAngle(
            Math.toRadians(270),  // X-axis rotation
            Math.toRadians(0),    // Y-axis rotation
            Math.toRadians(0)     // Z-axis rotation
        ));
        moveThePlane(armorStand,livingEntity);
    }

    private void moveThePlane(ArmorStand armorStand, LivingEntity target) {
    // Calculate initial trajectory ONCE at start
    Location startLocation = armorStand.getLocation().clone();
    Location initialTargetLocation = target.getLocation().clone();
    Vector initialDirection = initialTargetLocation.toVector().subtract(startLocation.toVector()).normalize();
    double initialDistance = startLocation.distance(initialTargetLocation);
    double continueDistance = initialDistance + 30; // Always fly 30 blocks past initial target position

    new BukkitRunnable() {
        double distanceFlown = 0;
        final double speed = 1.2; // Blocks per tick
        boolean hasPassedTarget = false;

        @Override
        public void run() {
            if (distanceFlown >= continueDistance || armorStand.isDead()) {
                armorStand.remove();
                this.cancel();
                return;
            }

            // Maintain original flight direction regardless of target movement
            Vector offset = initialDirection.clone().multiply(distanceFlown);
            Location newLocation = startLocation.clone().add(offset);

            // Keep original altitude trajectory

            newLocation.setY(target.getY()+30);
            armorStand.teleport(newLocation);

            // Rotate to face movement direction (fixed based on initial trajectory)
            armorStand.setRotation(
                (float) Math.toDegrees(Math.atan2(-initialDirection.getX(), initialDirection.getZ())),
                (float) Math.toDegrees(Math.asin(-initialDirection.getY()))
            );

            // Trigger explosion when crossing initial target position
            if (!hasPassedTarget && distanceFlown >= initialDistance) {
                hasPassedTarget = true;
                rayCastDownTheExplosion(initialTargetLocation, target);
            }

            distanceFlown += speed;
        }
    }.runTaskTimer(plugin, 0, 1);
}

    private void rayCastDownTheExplosion(Location startLocation, LivingEntity target) {
    ArmorStand projectile = startLocation.getWorld().spawn(startLocation, ArmorStand.class, stand -> {
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setCollidable(false);

        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(4);
        item.setItemMeta(meta);
        stand.getEquipment().setHelmet(item);
    });

    projectile.teleport(projectile.getLocation().add(0,30,0));


    new BukkitRunnable() {
        final double speed = 0.8; // Blocks per tick
        boolean hasExploded = false;

        @Override
        public void run() {
            if (hasExploded || projectile.isDead() || target.isDead()) {
                projectile.remove();
                this.cancel();
                return;
            }

            // ALWAYS get fresh target location
            Location targetLoc = target.getLocation().clone().add(0, 1, 0); // Aim at body center

            // Calculate direction to CURRENT target position
            Vector direction = targetLoc.toVector().subtract(projectile.getLocation().toVector()).normalize();

            // Update armor stand rotation to face target
            Location projLoc = projectile.getLocation();
            projLoc.setDirection(direction);
            projectile.teleport(projLoc);

            // Move towards target
            projectile.teleport(projectile.getLocation().add(direction.multiply(speed)));

            // Check collision with moving target
            if (projectile.getLocation().distanceSquared(targetLoc) < 4) { // 2 blocks range
                triggerExplosion(projectile.getLocation());
                hasExploded = true;
                projectile.remove();
                this.cancel();
            }
        }
    }.runTaskTimer(plugin, 0, 1);
}

private void triggerExplosion(Location loc) {
    loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
    loc.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.5f, 0.7f);
    Misc.damageNoTicksArea(loc, 25, 6);
}

    private static boolean isHoldingTheCorrectItem(Player player) {
        return isCorrectItem(player.getInventory().getItemInMainHand()) ||
               isCorrectItem(player.getInventory().getItemInOffHand());
    }

    private static boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME))) return false;
        return meta.getLore() != null && meta.getLore().contains(ITEM_LORE);
    }
}
