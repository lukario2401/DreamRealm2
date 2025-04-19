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
 import java.util.HashSet;
 import java.util.Set;

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
             LivingEntity airStrikeLocation = getAirStrikeLocationForJet(player);
             if (airStrikeLocation!=null){
                 airStrikeAtLocation(airStrikeLocation);
             }else{
                 player.sendMessage("Invalid Location");
             }

         }
          if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()== Action.LEFT_CLICK_BLOCK){

             getAirStrikeLocationForJetManyTargets(player);

          }
     }

    private void getAirStrikeLocationForJetManyTargets(Player player) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();
        Set<LivingEntity> processedEntities = new HashSet<>();

        for (float i = 0; i <= 64; i += 0.5f) {
            Location current = location.clone().add(direction.clone().multiply(i));
            current.getWorld().spawnParticle(Particle.CRIT, current, 1, 0, 0, 0, 0);

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(4)) {
                if (livingEntity.equals(player) || livingEntity instanceof ArmorStand) {
                    continue;
                }
                if (processedEntities.add(livingEntity)) {
                    airStrikeAtLocation(livingEntity);
                }
            }

            if (current.getBlock().getType() != Material.AIR) {
                break;
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

     private void rayCastDownTheExplosion(Location startLoc, LivingEntity target) {
        ArmorStand projectile = startLoc.getWorld().spawn(startLoc, ArmorStand.class, stand -> {
            stand.setGravity(false);
            stand.setVisible(false);
            stand.setMarker(true);
            stand.setCollidable(false);

            ItemStack item = new ItemStack(Material.FEATHER);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(4);
            item.setItemMeta(meta);
            stand.getEquipment().setHelmet(item);
        });

        new BukkitRunnable() {
            final double speed = 0.95;
            Location currentLoc = projectile.getLocation().clone();

            @Override
            public void run() {
                if (projectile.isDead() || target.isDead()) {
                    projectile.remove();
                    cancel();
                    return;
                }

                Location targetLoc = target.getLocation().clone().add(0, 1, 0);

                Vector direction = targetLoc.toVector().subtract(currentLoc.toVector()).normalize();

                currentLoc.add(direction.multiply(speed));
                projectile.teleport(currentLoc);

                if (currentLoc.distance(targetLoc) < 1) {
                    Misc.damageNoTicksArea(currentLoc, 21, 6);
                    currentLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, currentLoc, 1);
                    projectile.remove();
                    cancel();
                }
                if (projectile.isDead() || target.isDead()) {
                    projectile.remove();
                    cancel();
                    return;
                }

                 targetLoc = target.getLocation().clone().add(0, 1, 0);

                 direction = targetLoc.toVector().subtract(currentLoc.toVector()).normalize();

                currentLoc.add(direction.multiply(speed));
                projectile.teleport(currentLoc);

                if (currentLoc.distance(targetLoc) < 1) {
                    Misc.damageNoTicksArea(currentLoc, 21, 6);
                    currentLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, currentLoc, 1);
                    projectile.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
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