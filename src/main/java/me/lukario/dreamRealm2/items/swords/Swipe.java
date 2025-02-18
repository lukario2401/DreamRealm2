package me.lukario.dreamRealm2.items.swords;

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
import org.bukkit.util.Vector;

import java.util.Arrays;

public class Swipe implements Listener {

    private final Plugin plugin;

    public Swipe(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Swipe";//e668c6
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating #######";
    private static final Material ITEM_MATERIAL = Material.GOLDEN_SWORD;

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
public void swipeUse(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (!isHoldingTheCorrectItem(player)) return;

    if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {

        swipe(player,1,true);
        swipe(player,2,false);
        swipe(player,3,false);

    }
}
    private void swipe(Player player, Integer intt, Boolean isThereParticle){
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();
        Location location = eyeLocation.clone();

        location.add(direction.clone().multiply(intt));

        {location.setYaw(eyeLocation.getYaw()+90);}

//        location.getWorld().spawnParticle(Particle.FLAME,location,1,0,0,0,0);

        Vector directionOfNewOne = location.getDirection();
        //1132
        for (float i = -2; i <= 2; i+=0.25f){
            Location current = location.clone().add(directionOfNewOne.clone().multiply(i));

            if (isThereParticle){
                current.getWorld().spawnParticle(Particle.CRIT,current.add(0,i/2,0),1,0,0,0,0);
            }

            for (LivingEntity livingEntity : current.add(0,i/2,0).getNearbyLivingEntities(1)){
                if (!livingEntity.equals(player)){
                    livingEntity.damage(21,player);
                }
            }

        }
    }

    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Check if either hand holds the correct item
        if (isCorrectItem(mainHandItem)) {
            return true; // Correct item in main hand
        } else if (isCorrectItem(offHandItem)) {
            return true; // Correct item in off hand
        }

        return false; // No correct item in either hand
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
