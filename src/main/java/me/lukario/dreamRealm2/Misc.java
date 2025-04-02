package me.lukario.dreamRealm2;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Misc implements Listener {

    public static void damageNoTicks(LivingEntity livingEntity, double damage, Player player){
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

    public static void createInventoryItem(Inventory inventory, Material itemMaterial , Integer slot){
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("");
        meta.setLore(Arrays.asList(""));
        item.setItemMeta(meta);

        inventory.setItem(slot,item);
    }
}
