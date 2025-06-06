package me.lukario.dreamRealm2.items.guns_and_crates.guns;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.nio.file.LinkOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Glock implements Listener {

    private final Plugin plugin;

     public Glock(Plugin plugin) {
         this.plugin = plugin;
         cooldownManagement();
     }

     private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Glock 19";
     private static final String ITEM_LORE = ChatColor.YELLOW + "###########";
     private static final Material ITEM_MATERIAL = Material.STONE_HOE;

     private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();

     public static NamespacedKey AMMO_KEY;


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
     public void glockUsed(PlayerInteractEvent event){
         Player player = event.getPlayer();
         UUID uuid = player.getUniqueId();

         AMMO_KEY = new NamespacedKey(plugin, "ammo_count");

         if (cooldownLeft.get(uuid) == null){
             cooldownLeft.put(uuid,0f);
         }

         if (!isHoldingTheCorrectItem(player)){return;}
         if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){

             ItemStack gun = player.getInventory().getItemInMainHand();
             ItemMeta meta = gun.getItemMeta();
             if (meta == null){
                 player.sendMessage("meta is null");
                 return;
             }

             PersistentDataContainer data = meta.getPersistentDataContainer();

             int amountOfBullets = data.getOrDefault(AMMO_KEY, PersistentDataType.INTEGER, 0);
             int maxAmmo = 12;
             int availableAmmo = Misc.ItemAmountInInventory(player, "copper_ingot", 6);

             int loadAmount = Math.min(availableAmmo, maxAmmo - amountOfBullets);

            if (loadAmount > 0) {
                player.playSound(player, Sound.ITEM_ARMOR_EQUIP_IRON,3,1);

                int newAmmo = amountOfBullets + loadAmount;
                meta.getPersistentDataContainer().set(AMMO_KEY, PersistentDataType.INTEGER, newAmmo);

                // Cast to Damageable properly
                if (gun.getItemMeta() instanceof Damageable) {
                    Damageable damageable = (Damageable) gun.getItemMeta();
                    int maxDurability = 131;
                    int visualDamage = maxDurability - (int) ((newAmmo / 12f) * maxDurability);
                    damageable.setDamage(visualDamage);

                    ItemMeta newMeta = (ItemMeta) damageable;
                    newMeta.setUnbreakable(false); // Make durability bar visible
                    newMeta.setDisplayName(meta.getDisplayName());
                    newMeta.setLore(meta.getLore());
                    newMeta.setCustomModelData(meta.getCustomModelData());
                    newMeta.getPersistentDataContainer().set(AMMO_KEY, PersistentDataType.INTEGER, newAmmo);

                    gun.setItemMeta(newMeta);
                }
                player.sendMessage(newAmmo + " amount of bullets");
                itemToRemove(player, loadAmount);
            }else{
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 3,1);
            }
         }

         if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){

             if (cooldownLeft.get(uuid)==0){
                 cooldownLeft.put(uuid,4f);
                     ItemStack gun = player.getInventory().getItemInMainHand();
                     ItemMeta meta = gun.getItemMeta();

                     PersistentDataContainer data = meta.getPersistentDataContainer();
                     int bullets = data.getOrDefault(AMMO_KEY, PersistentDataType.INTEGER, 0);

                     if (bullets>0) {
                         player.playSound(player,Sound.ENTITY_BLAZE_SHOOT, 3 ,1);
                         rayCast(player);
                         meta.getPersistentDataContainer().set(AMMO_KEY, PersistentDataType.INTEGER, bullets-1);
                         gun.setItemMeta(meta);


                     if (meta instanceof ItemMeta) {
                          data = meta.getPersistentDataContainer();
                          bullets = data.getOrDefault(AMMO_KEY, PersistentDataType.INTEGER, 0);

                         if (bullets > 0) {


                             // Update ammo
                             data.set(AMMO_KEY, PersistentDataType.INTEGER, bullets);

                             // Update durability
                             if (gun.getItemMeta() instanceof Damageable) {
                                 Damageable damageable = (Damageable) gun.getItemMeta();
                                 int maxDurability = 131;
                                 int visualDamage = maxDurability - (int) ((bullets / 12f) * maxDurability);
                                 damageable.setDamage(visualDamage);

                                 ItemMeta newMeta = (ItemMeta) damageable;
                                 newMeta.setUnbreakable(false);
                                 newMeta.setDisplayName(meta.getDisplayName());
                                 newMeta.setLore(meta.getLore());
                                 newMeta.setCustomModelData(meta.getCustomModelData());
                                 newMeta.getPersistentDataContainer().set(AMMO_KEY, PersistentDataType.INTEGER, bullets);

                                 gun.setItemMeta(newMeta);
                             }

                             player.sendMessage(bullets + " bullets left");
                         } else {
                             player.sendMessage("out of ammo");
                         }
                     }}}}}




     private void itemToRemove(Player player, int amountToRemove){

         for (int j = 0; j < amountToRemove; j++){
             ItemStack[] contents = player.getInventory().getContents();

                for (int i = 0; i < contents.length; i++) {
                    ItemStack current = contents[i];
                    if (current == null || current.getType() != Material.COPPER_INGOT) continue;

                    ItemMeta meta = current.getItemMeta();
                    if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 6) {
                        if (current.getAmount() > 1) {
                            current.setAmount(current.getAmount() - 1);
                        } else {
                            contents[i] = null;
                        }
                        break;
                    }
                }

            player.getInventory().setContents(contents);
        }
     }

     private void rayCast(Player player){
         Location location = player.getEyeLocation();
         Vector direction = location.getDirection().normalize();

         for (float i = 0; i < 48; i+=0.5f){
             Location current = location.clone().add(direction.clone().multiply(i));

             current.getWorld().spawnParticle(Particle.ASH, current, 1 ,0,0,0,0);

             if (current.getBlock().getType()!=Material.AIR){
                 return;
             }

             for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                 if (!livingEntity.equals(player)){
                     Misc.damageNoTicks(livingEntity,9,player);
                     player.playSound(player,Sound.ENTITY_GENERIC_EXPLODE,1,1);
                     livingEntity.getWorld().spawnParticle(Particle.EXPLOSION,livingEntity.getLocation(),1,0,0,0,0);
                     return;
                 }
             }
         }
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