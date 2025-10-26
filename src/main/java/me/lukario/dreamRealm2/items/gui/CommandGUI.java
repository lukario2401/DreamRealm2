package me.lukario.dreamRealm2.items.gui;

import me.lukario.dreamRealm2.Misc;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Random;

public class CommandGUI implements Listener {

     private final Plugin plugin;

     public CommandGUI(Plugin plugin) {
         this.plugin = plugin;
     }


     public static void openCommandGUI(Player player){
          Inventory inventory = Bukkit.createInventory(null,54,"Command Center");

          Misc.createInventoryItem(inventory, Material.REDSTONE_TORCH,13,101401,"Air Strike");
          Misc.createInventoryItem(inventory, Material.OAK_BUTTON,21,101402,"Warden Heal");
          Misc.createInventoryItem(inventory, Material.OAK_BUTTON,22,101403,"Activate");

          player.openInventory(inventory);
          player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
     }


     @EventHandler
    public void onInventoryClickCommandGUI(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Command Center")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (item == null){return;}

            if (item.getItemMeta().getCustomModelData()==101401){
                 Location airStrikeLocation = getAirStrikeLocationForJet(player);
                 if (airStrikeLocation!=null){
                      airStrikeAtLocation(airStrikeLocation);
                 }
            }

            if (item.getItemMeta().getCustomModelData()==101402){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[type=warden] 1 200 true");
            }

            if (item.getItemMeta().getCustomModelData()==101403){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock 10748 59 -650 redstone_block");
                new BukkitRunnable(){
                    public void run(){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setblock 10748 59 -650 air");
                        this.cancel();
                    }
                }.runTaskLater(plugin,10);
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

     private void airStrikeAtLocation(Location location){
         location.setPitch(0);

         Random rand = new Random();
         int randomNumber = rand.nextInt(361);

         Location tempLocationForBackLocation = location.clone();
         tempLocationForBackLocation.setYaw(randomNumber);

         Location backLocation = tempLocationForBackLocation.clone().add(tempLocationForBackLocation.getDirection().normalize().clone().multiply(-60));
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
         moveThePlane(armorStand);
     }

     private void moveThePlane(ArmorStand armorStand){
         Location location = armorStand.getLocation();
         Vector direction = location.getDirection().normalize();


         new BukkitRunnable(){
             float i = 0;
             @Override
             public void run(){
                 Location current = location.clone().add(direction.clone().multiply(i));
                 armorStand.teleport(current);
                 if (i%5==0){
                     rayCastDownTheExplosion(armorStand.getLocation());
                 }
                 if (i > 120){
                     armorStand.remove();
                     this.cancel();
                 }
                 i+=1f;
             }
         }.runTaskTimer(plugin,0,1);
     }

     private void rayCastDownTheExplosion(Location startLocation) {
         ArmorStand armorStand = startLocation.getWorld().spawn(startLocation, ArmorStand.class);

         armorStand.setGravity(true);
         armorStand.setMarker(true);
         armorStand.setInvisible(true);

         ItemStack item = new ItemStack(Material.FEATHER);
         ItemMeta meta = item.getItemMeta();
         meta.setCustomModelData(4);
         item.setItemMeta(meta);

         armorStand.setItem(EquipmentSlot.HEAD,item);

         new BukkitRunnable(){
             @Override
             public void run(){
                 armorStand.teleport(armorStand.getLocation().add(0,-0.5,0));

                 if (armorStand.getLocation().add(0,-0.1,0).getBlock().getType()!=Material.AIR){

                     Misc.damageNoTicksArea(armorStand.getLocation(),25,6);
//                     armorStand.getLocation().getWorld().createExplosion(armorStand.getLocation(),11.5f,true,true);

                     armorStand.remove();
                     this.cancel();
                 }
             }
         }.runTaskTimer(plugin,0,1);

     }
}
