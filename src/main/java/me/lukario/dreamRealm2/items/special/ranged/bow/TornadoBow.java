package me.lukario.dreamRealm2.items.special.ranged.bow;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
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

public class TornadoBow implements Listener {

    private final Plugin plugin;

    public TornadoBow(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Tornado Bow";//e668c6
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Wind";
    private static final Material ITEM_MATERIAL = Material.IRON_HOE;

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
    public void tornadoBowUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!isHoldingTheCorrectItem(player)){return;}
        if (Action.LEFT_CLICK_AIR==event.getAction()||Action.LEFT_CLICK_BLOCK==event.getAction()){
            rayCastForTornadoBow(player);
        }
    }
    private void rayCastForTornadoBow(Player player){

        Location startingLocation = player.getEyeLocation();
        Vector direction = startingLocation.getDirection().normalize();

        float maxDistance = 64F;
        float stepSize = 0.5F;

        for (float i = 0F; i < maxDistance+0.5; i += stepSize){
            Location currentLocation = startingLocation.clone().add(direction.clone().multiply(i));


            for(LivingEntity livingEntity : currentLocation.getNearbyLivingEntities(2,2,2)){
                for(LivingEntity livingEntityIn : currentLocation.getNearbyLivingEntities(1,1,1)){
                    if (!livingEntityIn.equals(player)){
                        if (livingEntityIn.getNoDamageTicks()<5) {
                            livingEntityIn.teleport(currentLocation);
                            livingEntityIn.damage(50, player);
                            player.getWorld().playSound(currentLocation, Sound.ENTITY_ENDERMAN_TELEPORT,2,1);
                            player.getWorld().spawnParticle(Particle.CLOUD,currentLocation.add(0,-1,0),25,0.25,0.25,0.25,0);
                        }
                    }
                }
                if (!livingEntity.equals(player)){
                    if (livingEntity.getNoDamageTicks()<5) {
                        livingEntity.teleport(currentLocation);
                        livingEntity.damage(25, player);
                        player.getWorld().playSound(currentLocation, Sound.ENTITY_ENDERMAN_TELEPORT,2,1);
                        player.getWorld().spawnParticle(Particle.CLOUD,currentLocation,25,0.25,0.25,0.25,0);
                    }
                }
                if (!livingEntity.equals(player)){
                    i+=100;
                }
            }

            if (currentLocation.getBlock().getType()!=Material.AIR){

                for (LivingEntity livingEntity : currentLocation.getNearbyLivingEntities(4)){
                    if (!livingEntity.equals(player)){
                        livingEntity.teleport(currentLocation.add(direction.clone().multiply(-1)));
                    }
                }
                player.getWorld().playSound(currentLocation, Sound.ENTITY_ENDERMAN_TELEPORT,2,1);
                player.getWorld().spawnParticle(Particle.CLOUD,currentLocation,25,0.25,0.25,0.25,0);


                break;
            }

            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(200, 200, 255), 1);
            player.getWorld().spawnParticle(Particle.DUST, currentLocation, 1, 0.1, 0.1, 0.1, 0.01, dustOptions);
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
