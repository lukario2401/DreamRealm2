package me.lukario.dreamRealm2.items.guns_and_crates.guns;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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

    if (!isHoldingTheCorrectItem(player)) return;

    ItemStack gun = player.getInventory().getItemInMainHand();
    if (!isHoldingTheCorrectItem(player)) {
        return;
    }

    if (gun == null) return;

    ItemMeta gunMeta = gun.getItemMeta();
    NamespacedKey ammoKey = new NamespacedKey(plugin, "ammo_count");
    PersistentDataContainer data = gunMeta.getPersistentDataContainer();

    // Right-click: Load ammo
    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
        int loaded = data.getOrDefault(ammoKey, PersistentDataType.INTEGER, 0);
        int toLoad = Math.min(12 - loaded, Misc.ItemAmountInInventory(player, "copper_ingot", 12));

        if (toLoad > 0){
            RemoveCustomCopperAmmo(player, toLoad); // You'll define this method
            data.set(ammoKey, PersistentDataType.INTEGER, loaded + toLoad);
            gun.setItemMeta(gunMeta);
            player.sendMessage("Loaded " + toLoad + " bullets. Now loaded: " + (loaded + toLoad));
        } else {
            player.sendMessage("No ammo to load.");
        }

         ItemStack item = new ItemStack(Material.COPPER_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(2);
            item.setItemMeta(meta);

             player.getInventory().addItem(item);
    }

    // Left-click: Shoot
    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
        if (cooldownLeft.get(uuid)==0){
            cooldownLeft.put(uuid, 4f);

            int loaded = data.getOrDefault(ammoKey, PersistentDataType.INTEGER, 0);
            if (loaded > 0){
                rayCast(player);
                data.set(ammoKey, PersistentDataType.INTEGER, loaded - 1);
                gun.setItemMeta(gunMeta);
                player.sendMessage("Fired! Remaining bullets: " + (loaded - 1));
            } else {
                player.sendMessage("Out of ammo!");
            }
        }
    }
}

public static void RemoveCustomCopperAmmo(Player player, int amountToRemove) {
    ItemStack[] contents = player.getInventory().getContents();

    for (int i = 0; i < contents.length && amountToRemove > 0; i++) {
        ItemStack current = contents[i];
        if (current == null || current.getType() != Material.COPPER_INGOT) continue;

        ItemMeta meta = current.getItemMeta();
        if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 2) {
            int stackAmount = current.getAmount();
            if (stackAmount <= amountToRemove) {
                amountToRemove -= stackAmount;
                contents[i] = null;
            } else {
                current.setAmount(stackAmount - amountToRemove);
                amountToRemove = 0;
            }
        }
    }

    player.getInventory().setContents(contents);
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
