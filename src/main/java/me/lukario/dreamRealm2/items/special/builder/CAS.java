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

import java.util.Arrays;
import java.util.Random;

public class CAS implements Listener {

    private final Plugin plugin;

     public CAS(Plugin plugin) {
         this.plugin = plugin;
     }

     Random random = new Random();

     private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Close Air Support";
     private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ###########";
     private static final Material ITEM_MATERIAL = Material.BREEZE_ROD;

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
     public void casUsed(PlayerInteractEvent event){
         Player player = event.getPlayer();

         if (!isHoldingTheCorrectItem(player)){
             return;
         }

         if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()== Action.RIGHT_CLICK_BLOCK){
             LivingEntity airStrikeLocation = getAirStrikeLocationForJet(player);
             if (airStrikeLocation!=null){
                 airStrikeAtLocation(airStrikeLocation);
             }else{
                 player.sendMessage("Invalid Location");
             }

         }
     }

     private LivingEntity getAirStrikeLocationForJet(Player player){
         Location location = player.getEyeLocation();
         Vector direction = location.getDirection().normalize();

         Location current = null;
         LivingEntity livingEntityFor = null;

         for (float i = 0; i <= 64; i+=0.5f){
             current = location.clone().add(direction.clone().multiply(i));
             current.getWorld().spawnParticle(Particle.CRIT,current,1,0,0,0,0);

             if (current.getBlock().getType()!=Material.AIR){
                 return livingEntityFor;
             }

             for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                 if (!livingEntity.equals(player)){
                     if (livingEntity instanceof ArmorStand){}else{
                         return livingEntity;
                     }
                 }
             }
         }

         return livingEntityFor;
     }

     private void airStrikeAtLocation(LivingEntity livingEntity){
         Location location = livingEntity.getLocation();
         location.setPitch(0);

         Location tempLocationForBackLocation = location.clone();
         tempLocationForBackLocation.setYaw(random.nextInt(361));

         Location backLocation = tempLocationForBackLocation.clone().add(tempLocationForBackLocation.getDirection().normalize().clone().multiply(-60));
         backLocation.add(0,15,0);

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

         moveThePlane(armorStand, livingEntity);
     }

     private void moveThePlane(ArmorStand armorStand, LivingEntity livingEntity){
         new BukkitRunnable(){
             float i = 0;
             boolean hasCastedDown = false;
             Vector globalDirection = null;
             Location startLocation = armorStand.getLocation();
             @Override
             public void run(){

                 Location endLocation = livingEntity.getLocation();
                 endLocation.setY(armorStand.getY());

                 double distance = startLocation.distance(endLocation);
                 Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();

                 if (!hasCastedDown){
                     if (armorStand.getLocation().distance(endLocation) > 1){
                         Location current = startLocation.clone().add(direction.clone().multiply(i));
                         armorStand.teleport(current);
                     }else{
                         globalDirection = direction.clone();
                         rayCastDownTheExplosion(armorStand.getLocation(),livingEntity);
                         hasCastedDown=true;
                     }
                 }

                 if (hasCastedDown){
                     armorStand.teleport(startLocation.clone().add(globalDirection.clone().multiply(i)));
                 }

                 if (i > 120){
                     armorStand.remove();
                     this.cancel();
                 }

                 i+=0.5f;

                 endLocation = livingEntity.getLocation();
                 endLocation.setY(armorStand.getY());

                  distance = startLocation.distance(endLocation);
                  direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();

                 if (!hasCastedDown){
                     if (armorStand.getLocation().distance(endLocation) > 1){
                         Location current = startLocation.clone().add(direction.clone().multiply(i));
                         armorStand.teleport(current);
                     }else{
                         globalDirection = direction.clone();
                         rayCastDownTheExplosion(armorStand.getLocation(),livingEntity);
                         hasCastedDown=true;
                     }
                 }

                 if (hasCastedDown){
                     armorStand.teleport(startLocation.clone().add(globalDirection.clone().multiply(i)));
                 }

                 if (i > 120){
                     armorStand.remove();
                     this.cancel();
                 }

                 i+=0.5f;
             }
         }.runTaskTimer(plugin,0,1);
     }

     private void rayCastDownTheExplosion(Location location, LivingEntity livingEntity1){
         for (LivingEntity livingEntity : livingEntity1.getLocation().add(0,1,0).getNearbyLivingEntities(3)){
             for (float i = 0; i < 4; i ++){
                missileLeftClicked(location.add(0,0,0), livingEntity);
             }
         }
     }

     private void missileLeftClicked(Location location, LivingEntity livingEntity){

        Location startLocation = location.add(0, 1, 0).clone();

        // Calculate core directions
        float yaw = startLocation.getYaw();
        double radianYaw = Math.toRadians(yaw);
        Vector forward = new Vector(-Math.sin(radianYaw), 0, Math.cos(radianYaw));
        Vector right = new Vector(forward.getZ(), 0, -forward.getX()).normalize();
        Vector left = right.clone().multiply(-1);

        // Create angled trajectories
        double angleVariation = Math.toRadians(random.nextInt(180) - 90); // -20° to +20° variation
        Vector stepDirection = random.nextBoolean()
            ? left.clone().rotateAroundY(angleVariation)  // Left with random back/forward component
            : right.clone().rotateAroundY(angleVariation); // Right with random back/forward component

        // Configure missile path
        stepDirection.normalize();
        Vector step = stepDirection.multiply(0.3);
        int totalSteps = 20+random.nextInt(20); // 12 blocks / 0.3 per step
        Location current = startLocation.clone();

        // Visual trajectory
        for (int i = 0; i < totalSteps; i++) {
            // Add gradual descent
            current.add(step).subtract(0, i * 0.015, 0); // 0.015 y-drop per step
            current.getWorld().spawnParticle(Particle.FIREWORK, current, 1, 0, 0, 0, 0);
        }

        // Final impact
        twoPlaceRayCast(current, livingEntity);

    }

    private void twoPlaceRayCast(Location location, LivingEntity livingEntity){
        Location startLocation = location;

        new BukkitRunnable(){
            float i =0;
            @Override
            public void run(){
                Location endLocation = livingEntity.getLocation();

                double distance = startLocation.distance(endLocation);
                Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();


                Location current = startLocation.clone().add(direction.clone().multiply(i));

                current.getWorld().spawnParticle(Particle.FIREWORK,current,1,0,0,0,0);

                if (i>distance-1){
                    for (LivingEntity livingEntity1: current.getNearbyLivingEntities(3)){
                        if (!livingEntity1.isDead()){
                            Misc.damageNoTicks(livingEntity1,32);
                        }
                        current.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,current,1,0,0,0,0);
                        current.getWorld().playSound(current,Sound.ENTITY_GENERIC_EXPLODE,3,1);

                    }
                    for (LivingEntity livingEntity1: current.getNearbyLivingEntities(2)){
                        if (!livingEntity1.isDead()){
                            Misc.damageNoTicks(livingEntity1,32);
                        }
                    }
                    for (LivingEntity livingEntity1: current.getNearbyLivingEntities(1)){
                        if (!livingEntity1.isDead()){
                            Misc.damageNoTicks(livingEntity1,32);
                        }
                    }
                    this.cancel();
                    return;
                }
                i+=1f;
            }
        }.runTaskTimer(plugin,0,1);
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
