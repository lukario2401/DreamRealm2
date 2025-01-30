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

public class Katana implements Listener {

    private final Plugin plugin;

    public Katana(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Katana";//e668c6
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ######";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

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
    public void katanaUse(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (!isHoldingTheCorrectItem(player)) return;

    if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
        player.sendMessage("click");

        Location location = player.getLocation();
        Vector direction = location.getDirection().normalize();

//        direction = direction.clone().multiply(2);

        // Get the left direction by rotating the direction vector by -90 degrees (left)
        location.add(direction.clone().multiply(1.0));

        Vector leftDirection = new Vector(-direction.getZ(), 0, direction.getX()).normalize();

        // Adjust the position to be slightly to the left

        // Damage nearby entities except the player

        for (float i = -1.5f; i <= 1.5; i+=0.05f){

            float height = i/2;

            Location newLocation = location.clone().add(leftDirection.multiply(i)).add(0, 1+height, 0); // Adjust height slightly
//            newLocation = newLocation.setDirection(newLocation.getDirection().multiply(2).normalize());

            for (LivingEntity livingEntity : newLocation.getNearbyLivingEntities(0.1)) {
                if (!livingEntity.equals(player)) {
                    livingEntity.damage(21, player);
                }
            }

            // Spawn the particle exactly to the left
            player.getWorld().spawnParticle(Particle.FLAME, newLocation, 1, 0, 0, 0, 0);
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
