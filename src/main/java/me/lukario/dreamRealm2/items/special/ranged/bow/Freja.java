package me.lukario.dreamRealm2.items.special.ranged.bow;

import me.lukario.dreamRealm2.Misc;
 import net.md_5.bungee.api.ChatColor;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.Particle;
 import org.bukkit.Sound;
 import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
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

 import java.util.*;

 public class Freja implements Listener {

     private final Plugin plugin;

     public Freja(Plugin plugin) {
         this.plugin = plugin;
         cooldownManagement();
     }

     private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Freja";
     private static final String ITEM_LORE = ChatColor.YELLOW + "##########";
     private static final Material ITEM_MATERIAL = Material.CROSSBOW;

     private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();
     private static final HashMap<UUID,Float> frejaBoltCount = new HashMap<>();

     private static final HashMap<UUID,Float> cooldownRight = new HashMap<>();

      private void cooldownManagement(){
         new BukkitRunnable(){
             @Override
             public void run(){
                 Set<UUID> keyLeft = cooldownLeft.keySet();
                 for (UUID uuid : keyLeft){
                     if (cooldownLeft.get(uuid)>0){
                         cooldownLeft.put(uuid,cooldownLeft.get(uuid)-1);
                     }
                 }
                 Set<UUID> keyRight = cooldownRight.keySet();
                 for (UUID uuid : keyRight){
                     if (cooldownRight.get(uuid)>0){
                         cooldownRight.put(uuid,cooldownRight.get(uuid)-1);
                     }
                 }
             }
         }.runTaskTimer(plugin,0,1);
     }

      public static ItemStack createItem() {
         ItemStack item = new ItemStack(ITEM_MATERIAL);
         ItemMeta meta = item.getItemMeta();
         if (meta != null) {
             meta.setDisplayName(ITEM_NAME);
             meta.setLore(Arrays.asList(ITEM_LORE));
             meta.setUnbreakable(true);
             meta.setCustomModelData(2);
             item.setItemMeta(meta);
         }
         return item;
     }

     @EventHandler
     public void frejaUsed(PlayerInteractEvent event){
         Player player = event.getPlayer();
         UUID uuid = player.getUniqueId();

         if (cooldownLeft.get(uuid)==null){
             cooldownLeft.put(uuid,0f);
         }
         if (cooldownRight.get(uuid)==null){
             cooldownRight.put(uuid,0f);
         }

         if (frejaBoltCount.get(uuid)==null){
             frejaBoltCount.put(uuid,12f);
         }

         if (!isHoldingTheCorrectItem(player)){return;}

         if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
             if (cooldownLeft.get(uuid)==0){

                 if (frejaBoltCount.get(uuid)>0){

                     frejaBoltCount.put(uuid,frejaBoltCount.get(uuid)-1f);
                     frejaLeftClick(player);
                     player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
                     player.sendMessage( ChatColor.BLUE+"You have: " + frejaBoltCount.get(uuid) + " Bolts");

                 }else{
                     player.sendMessage(ChatColor.YELLOW+"Freja Reloading");
                     player.playSound(player,Sound.BLOCK_ANVIL_PLACE,1,1);
                     cooldownLeft.put(uuid,20f);
                     frejaBoltCount.put(uuid,12f);
                 }

             }else{
                 player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownLeft.get(uuid)/20)+"s");
                 player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
             }
         }
         if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK){
             if (cooldownRight.get(uuid)==0){

             if (frejaBoltCount.get(uuid)>8){
                 frejaBoltCount.put(uuid,12f);
             }else{
                 frejaBoltCount.put(uuid,frejaBoltCount.get(uuid)+4f);
             }

             frejaRightClick(player);

             player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
             cooldownRight.put(uuid,30f);

             }else{
                 player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownRight.get(uuid)/20)+"s");
                 player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
             }
         }
         event.setCancelled(true);
     }

     private void frejaLeftClick(Player player){

         Location location = player.getEyeLocation();
         Vector direction = location.getDirection().normalize();

         new BukkitRunnable(){
             @Override
             public void run(){
                 for (float i = 0; i<=32f; i+=0.5f){

                     Location current = location.clone().add(direction.clone().multiply(i));
                     current.getWorld().spawnParticle(Particle.SOUL,current,1,0,0,0,0);

                    if (current.getBlock().getType()!=Material.AIR){
                        i+=256;
                    }

                     for (LivingEntity livingEntity : current.getNearbyLivingEntities(0.6f)){
                         if (!livingEntity.equals(player)){
                             if (livingEntity instanceof ArmorStand){
                             }else{

                                 Misc.damageNoTicks(livingEntity,52, player);
                                 player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);

                                 i+=256;
                                 this.cancel();
                             }
                         }
                     }
                 }
                 this.cancel();
             }
         }.runTaskTimer(plugin,0,1);
     }

     private void frejaRightClick(Player player){

         Location location = player.getEyeLocation();
         Vector direction = location.getDirection().normalize();
         LivingEntity livingEntityToGetStuckToo = null;
         Location globalLocation = null;

         ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

         armorStand.setInvulnerable(true);
         armorStand.setSmall(true);
         armorStand.setMarker(true);
         armorStand.setInvisible(true);
         armorStand.setGravity(false);
         armorStand.setBasePlate(false);

         ItemStack item = new ItemStack(Material.FEATHER);
         ItemMeta meta = item.getItemMeta();
         meta.setCustomModelData(2);
         item.setItemMeta(meta);

         armorStand.setItem(EquipmentSlot.HEAD,new ItemStack(Material.TNT));

         for (float i = 0; i<=48f; i+=1f){

             Location current = location.clone().add(direction.clone().multiply(i));
             armorStand.getLocation().getWorld().spawnParticle(Particle.SOUL,armorStand.getLocation().add(0,1,0),1,0.1,0.1,0.1,0);

             if (current.getBlock().getType()!=Material.AIR){
                 i+=256;
             }

             for (LivingEntity livingEntity : current.getNearbyLivingEntities(1.5f)){
                 if (!livingEntity.equals(player)){
                     if (!livingEntity.equals(armorStand)){

                         livingEntityToGetStuckToo=livingEntity;
                         break;

                     }
                 }
             }

             globalLocation=current;
         }
         frejaBoltStuck(player,armorStand,globalLocation,livingEntityToGetStuckToo);
     }

    private void frejaBoltStuck(Player player, ArmorStand armorStand, Location globalLocation, LivingEntity target) {
    new BukkitRunnable() {
        int ticks = 0;
        boolean exploded = false; // Track explosion state

        @Override
        public void run() {
            // Update position
            if (target != null && !target.isDead()) {
                armorStand.teleport(target.getLocation());
            } else {
                armorStand.teleport(globalLocation);
            }

            // Show tracking particles
            armorStand.getWorld().spawnParticle(
                Particle.SOUL,
                armorStand.getLocation().add(0, 1, 0),
                1, 0.3, 0.3, 0.3, 0
            );
            armorStand.setBodyYaw(armorStand.getBodyYaw()+36);

            // Single explosion trigger
            if (ticks++ == 40 && !exploded) {
                exploded = true;

                // Explosion effects
                armorStand.getWorld().spawnParticle(
                    Particle.EXPLOSION_EMITTER,
                    armorStand.getLocation(),
                    5, 0.2, 0.2, 0.2, 0
                );
                armorStand.getWorld().playSound(
                    armorStand.getLocation(),
                    Sound.ENTITY_GENERIC_EXPLODE,
                    3, 1
                );

                // Damage nearby entities once
                for (LivingEntity entity : armorStand.getLocation().getNearbyLivingEntities(4)) {
                    if (entity instanceof Creature) {
                        Misc.damageNoTicks(entity, 62, player);
                    }
                }

                // Cleanup
                armorStand.remove();
                this.cancel();
            }
        }
    }.runTaskTimer(plugin, 0, 1);
}

     private static boolean isHoldingTheCorrectItem(Player player) {
         ItemStack mainHandItem = player.getInventory().getItemInMainHand();
         ItemStack offHandItem = player.getInventory().getItemInOffHand();

         if (isCorrectItem(mainHandItem)) {
             return true;
         } else return isCorrectItem(offHandItem);
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