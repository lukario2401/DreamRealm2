package me.lukario.dreamRealm2.items.swords.swing;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class GiantSword implements Listener {

    private static final String ITEM_NAME = net.md_5.bungee.api.ChatColor.of("#D88F07") + "Giant Sword";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating a protector of Wither";
    private static final Material MATERIAL_TYPE = Material.GOLDEN_SWORD;

    private static int slashStage = 0;

    public static ItemStack createItemGiantSword() {
        ItemStack item = new ItemStack(MATERIAL_TYPE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME); // Uses net.md_5.bungee.api.ChatColor
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    private static void slash(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction()== Action.LEFT_CLICK_BLOCK){

            player.sendMessage("Your count is: " + slashStage);

            int damageDealtByPlayer = slashStage;
            damageDealtByPlayer = damageDealtByPlayer +21;

            dealAreaDamage(player,3,damageDealtByPlayer,0);
            dealAreaDamage(player,2.75,damageDealtByPlayer,1);
            dealAreaDamage(player,2.5,damageDealtByPlayer,2);
            dealAreaDamage(player,2.25,damageDealtByPlayer,3);
            dealAreaDamage(player,2,damageDealtByPlayer,4);
            dealAreaDamage(player,1.75,damageDealtByPlayer,5);
            dealAreaDamage(player,1.5,damageDealtByPlayer,6);
            dealAreaDamage(player,1.25,damageDealtByPlayer,7);
            dealAreaDamage(player,1,damageDealtByPlayer,8);
            dealAreaDamage(player,0.75,damageDealtByPlayer,9);
            dealAreaDamage(player,0.5,damageDealtByPlayer,10);
            dealAreaDamage(player,0.25,damageDealtByPlayer,11);


            if(slashStage>8){
                slashStage = 0;
            }else{
                slashStage++;
            }
       }
    }

    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != MATERIAL_TYPE) return false;

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

    private static void dealAreaDamage(Player player, double radius, double damage,double blocksForward) {
        // Get the player's current location
        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize(); // Normalized direction vector

        // Get nearby entities within the radius
        for (Entity entity : player.getWorld().getNearbyEntities(playerLocation.add(direction.multiply(blocksForward)), radius, radius, radius)) {
            // Check if the entity is a living entity and not the player themselves
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity livingEntity = (LivingEntity) entity;

                // Deal damage
                livingEntity.damage(damage, player);

                // Optional: Notify the player
//                player.sendMessage(ChatColor.YELLOW + "You dealt " + damage + " damage to " + entity.getName());
            }
        }
    }
}

