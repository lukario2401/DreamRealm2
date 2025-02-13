package me.lukario.dreamRealm2.items.special.magic;

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

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ShareHealth implements Listener {

    private final Plugin plugin;

    public ShareHealth(Plugin plugin) {
        this.plugin = plugin;
        startCooldownTask();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Shared Health";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ####";
    private static final Material ITEM_MATERIAL = Material.HEART_OF_THE_SEA;

    private static final HashMap<UUID,Float> cooldowns = new HashMap<>();

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

     private void startCooldownTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<UUID> keys = cooldowns.keySet();
                for (UUID uuid : keys){
                    if (cooldowns.get(uuid)>0){
                        cooldowns.put(uuid,cooldowns.get(uuid)-1);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }


    @EventHandler
    public void shareHealthUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (!isHoldingTheCorrectItem(player)){return;}
        if (Action.RIGHT_CLICK_BLOCK==event.getAction()||Action.RIGHT_CLICK_AIR==event.getAction()){

            if (cooldowns.get(player.getUniqueId())==null){
                cooldowns.put(player.getUniqueId(),0f);
            }
            if (cooldowns.get(player.getUniqueId())==0){
            Location location = player.getEyeLocation();
            Vector direction = location.getDirection().normalize();

            for (float i = 0; i<=32;i +=0.5f){
                Location currentLocation = location.clone().add(direction.clone().multiply(i));

                currentLocation.getWorld().spawnParticle(Particle.SOUL,currentLocation,1,0,0,0,0);

                for (LivingEntity livingEntity : currentLocation.getNearbyLivingEntities(1)){
                    if (!livingEntity.equals(player)){

                            twoEntityRayCast(player,livingEntity);
                            i+=200;
                        }
                    }
                }
                cooldowns.put(player.getUniqueId(),200f);
            }else{
                player.sendMessage("Your cooldown is: " + cooldowns.get(player.getUniqueId())/20);
            }
        }
    }

    private void twoEntityRayCast(Player player, LivingEntity livingEntity){

        new BukkitRunnable(){
            int aliveTime =0;
            @Override
            public void run(){
                if (livingEntity.isDead()){
                    aliveTime+=200;
                }
                if (aliveTime>=200){
                    this.cancel();
                }

                Location startLocation = player.getLocation().add(0,1,0);
                Location endLocation = livingEntity.getLocation().add(0,1,0);

                Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();
                double distance = startLocation.distance(endLocation);

                Location current = startLocation.clone();
                for (float i = 0; i <= distance; i+= 0.5f){

                    current.getWorld().spawnParticle(Particle.HEART,current,1,0,0,0,0);

                    if (!livingEntity.isDead()&&!player.isDead()){
                        if (livingEntity.getHealth()>player.getHealth()){
                            if (livingEntity.getHealth()>player.getMaxHealth()){
                                player.setHealth(player.getMaxHealth());
                            }else{
                                player.setHealth(livingEntity.getHealth());
                            }
                        }else{
                            if (player.getHealth()>livingEntity.getMaxHealth()){
                                livingEntity.setHealth(livingEntity.getMaxHealth());
                            }else {
                                livingEntity.setHealth(player.getHealth());
                            }
                        }
                    }

                    current.add(direction.clone().multiply(0.5));
                }
                aliveTime+=1;
            }
        }.runTaskTimer(plugin,0,10);
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
