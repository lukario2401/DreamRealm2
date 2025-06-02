package me.lukario.dreamRealm2.items.guns_and_crates.guns;

import me.lukario.dreamRealm2.Misc;
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

     private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Glock";
     private static final String ITEM_LORE = ChatColor.YELLOW + "###########";
     private static final Material ITEM_MATERIAL = Material.STONE_HOE;

     private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();

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

         if (cooldownLeft.get(uuid) == null){
             cooldownLeft.put(uuid,0f);
         }

         if (!isHoldingTheCorrectItem(player)){return;}
         if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){

            ItemStack item = new ItemStack(Material.COPPER_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(2);
            item.setItemMeta(meta);

             player.getInventory().addItem(item);
         }
         if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
             if (cooldownLeft.get(uuid)==0){
                 cooldownLeft.put(uuid,4f);
                 if (Misc.ItemAmountInInventory(player,"copper_ingot",2)>0){
                    rayCast(player);

                    ItemStack[] contents = player.getInventory().getContents();

                    for (int i = 0; i < contents.length; i++) {
                        ItemStack current = contents[i];
                        if (current == null || current.getType() != Material.COPPER_INGOT) continue;

                        ItemMeta meta = current.getItemMeta();
                        if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 2) {
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
         }
     }

     private void rayCast(Player player){
         Location location = player.getEyeLocation();
         Vector direction = location.getDirection().normalize();

         for (float i = 0; i < 64; i+=0.5f){
             Location current = location.clone().add(direction.clone().multiply(i));

             current.getWorld().spawnParticle(Particle.ASH, current, 1 ,0,0,0,0);


             for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                 if (!livingEntity.equals(player)){
                     Misc.damageNoTicks(livingEntity,9,player);
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