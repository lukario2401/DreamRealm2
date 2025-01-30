package me.lukario.dreamRealm2.items.special.ranged.misc;

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

public class FlameThrower implements Listener {

    private final Plugin plugin;

    public FlameThrower(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "FlameThrower";//e668c6
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Fire";
    private static final Material ITEM_MATERIAL = Material.BLAZE_POWDER;

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
    public void flameThrowerUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!isHoldingTheCorrectItem(player)){return;};
        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()||Action.LEFT_CLICK_BLOCK==event.getAction()||Action.LEFT_CLICK_AIR==event.getAction()){

            Location startLocation = player.getEyeLocation();
            Vector direction = startLocation.getDirection().normalize();
            float stepSize = 0.5F;

            for (float i = 0; i < 16; i+=stepSize){
                Location currentLocation = startLocation.clone().add(direction.clone().multiply(i));

                player.getWorld().spawnParticle(Particle.FLAME,currentLocation,25,i/8,i/8,i/8,0);

                for (LivingEntity livingEntity : currentLocation.getNearbyLivingEntities(i/3)){
                    if (!livingEntity.equals(player)){
                        livingEntity.damage(64/i,player);
                        livingEntity.setFireTicks(200);
                    }
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
