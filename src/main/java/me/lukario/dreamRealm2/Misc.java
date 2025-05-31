package me.lukario.dreamRealm2;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class Misc implements Listener {

    public static void damageNoTicks(LivingEntity livingEntity, double damage, Player player){
        if (livingEntity.isDead()){
            return;
        }
        if (livingEntity instanceof Player){
            Player player1 = (Player) livingEntity;
            if (player1.getGameMode()== GameMode.CREATIVE||player1.getGameMode()== GameMode.SPECTATOR){
                return;
            }
        }
        if (livingEntity instanceof ArmorStand){}else {
            if (livingEntity.getHealth()>damage){
                livingEntity.setHealth(livingEntity.getHealth()-damage);
                livingEntity.playHurtAnimation(1);
                if (livingEntity.getHurtSound()!=null){
                    livingEntity.getWorld().playSound(livingEntity.getLocation(),livingEntity.getHurtSound(),1,1);
                }
                Creature creature = (Creature) livingEntity;
                creature.setTarget(player);
            }else{
                livingEntity.setHealth(0);
                Creature creature = (Creature) livingEntity;
                creature.setTarget(player);
            }
        }
    }
    public static void damageNoTicks(LivingEntity livingEntity, double damage){
        if (livingEntity.isDead()){
            return;
        }
        if (livingEntity instanceof Player){
            Player player1 = (Player) livingEntity;
            if (player1.getGameMode() == GameMode.CREATIVE||player1.getGameMode()== GameMode.SPECTATOR){
                return;
            }
        }
        if (livingEntity instanceof ArmorStand){}else {
            if (livingEntity.getHealth()>damage){
                livingEntity.setHealth(livingEntity.getHealth()-damage);
                livingEntity.playHurtAnimation(1);
                if (livingEntity.getHurtSound()!=null){
                    livingEntity.getWorld().playSound(livingEntity.getLocation(),livingEntity.getHurtSound(),1,1);
                }
            }else{
                livingEntity.setHealth(0);
            }
        }
    }

    public static void damageNoTicksArea(Location location, double damage, int radius){

        location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,location.add(0,1,0),5,0.2,0.2,0.2,0);
        location.getWorld().playSound(location.add(0,1,0), Sound.ENTITY_GENERIC_EXPLODE,5,1);

        for (float i = 0; i <= radius; i+=1){
            for (LivingEntity livingEntity : location.getNearbyLivingEntities(i)){
                if (livingEntity instanceof ArmorStand){}else{
                    if (!livingEntity.isDead()){
                        Misc.damageNoTicks(livingEntity,damage);
                    }
                }
            }
        }

    }

    public static void createInventoryItem(Inventory inventory, Material itemMaterial, Integer slot, Integer customModelData, String name, String lore, String loreTwo){
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setCustomModelData(customModelData);
        meta.setLore(Arrays.asList(lore,loreTwo));
        item.setItemMeta(meta);

        inventory.setItem(slot,item);
    }

    public static void createInventoryItem(Inventory inventory, Material itemMaterial, Integer slot, Integer customModelData, String name, String lore){
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setCustomModelData(customModelData);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        inventory.setItem(slot,item);
    }


    public static void createInventoryItem(Inventory inventory, Material itemMaterial, Integer slot, Integer customModelData, String name){
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setCustomModelData(customModelData);
        meta.setLore(Arrays.asList(""));
        item.setItemMeta(meta);

        inventory.setItem(slot,item);
    }

    public static void createInventoryItem(Inventory inventory, Material itemMaterial, Integer slot, Integer customModelData){
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("");
        meta.setCustomModelData(customModelData);
        meta.setLore(Arrays.asList(""));
        item.setItemMeta(meta);

        inventory.setItem(slot,item);
    }
    public static Location moveLocationInAnyDirection(Location location1,float distance){
        Location location = location1.clone();
        Vector oldDirection = location.getDirection();
        location.setYaw(location.getYaw()+90);

        Vector direction = location.getDirection().normalize();
        location.add(direction.multiply(distance));
        location.setDirection(oldDirection);

        return location;
    }
    public static Location moveLocationInAnyDirection(Location location1, float distance, boolean rightOrLeft){
        Location location = location1.clone();
        Vector oldDirection = location.getDirection();
        if (rightOrLeft){
            location.setYaw(location.getYaw()+90);
        }else{
            location.setYaw(location.getYaw()-90);
        }

        Vector direction = location.getDirection().normalize();
        location.add(direction.multiply(distance));
        location.setDirection(oldDirection);

        return location;
    }
    public static Location moveLocationInAnyDirection(Location location1,float distance, float rotation){
        Location location = location1.clone();
        Vector oldDirection = location.getDirection();
        location.setYaw(location.getYaw()+rotation);

        Vector direction = location.getDirection().normalize();
        location.add(direction.multiply(distance));
        location.setDirection(oldDirection);

        return location;
    }

    public static int ItemAmountInInventory(Player player, String itemName) {
        int count = 0;

        Material material;
        try {
            material = Material.valueOf(itemName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return -1;
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != material) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                count += item.getAmount();
            }
        }

        return count;
    }
    public static int ItemAmountInInventory(Player player, String itemName, int customModelData) {
        int count = 0;

        Material material;
        try {
            material = Material.valueOf(itemName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return -1;
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != material) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == customModelData) {
                count += item.getAmount();
            }
        }
        return count;
    }

}
