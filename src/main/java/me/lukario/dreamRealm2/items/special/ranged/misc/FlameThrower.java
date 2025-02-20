package me.lukario.dreamRealm2.items.special.ranged.misc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static me.lukario.dreamRealm2.Misc.damageNoTicks;

public class FlameThrower implements Listener {

    private final Plugin plugin;

    public FlameThrower(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
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

    private static HashMap<UUID,Float> cooldown = new HashMap<>();

    private void cooldownManagement(){
        new BukkitRunnable(){
            @Override
            public void run(){

                Set<UUID> key = cooldown.keySet();

                for (UUID uuid : key){
                    if (cooldown.get(uuid)>0){
                        cooldown.put(uuid,cooldown.get(uuid)-1);
                    }
                }

            }
        }.runTaskTimer(plugin,0,1);
    }

    @EventHandler
    public void flameThrowerUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cooldown.get(uuid)==null){
            cooldown.put(uuid,0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;};
        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()||Action.LEFT_CLICK_BLOCK==event.getAction()||Action.LEFT_CLICK_AIR==event.getAction()){

//            Location startLocation = player.getEyeLocation();
//            Vector direction = startLocation.getDirection().normalize();
//            float stepSize = 0.5F;
//
//            for (float i = 0; i < 16; i+=stepSize){
//                Location currentLocation = startLocation.clone().add(direction.clone().multiply(i));
//
//                player.getWorld().spawnParticle(Particle.FLAME,currentLocation,25,i/8,i/8,i/8,0);
//
//                for (LivingEntity livingEntity : currentLocation.getNearbyLivingEntities(i/3)){
//                    if (!livingEntity.equals(player)){
//                        livingEntity.damage(64/i,player);
//                        livingEntity.setFireTicks(200);
//                    }
//                }
//            }

            if (cooldown.get(uuid)==0){

                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);

                flameThrowerShortRanged(player,0,0,8);
                flameThrowerShortRanged(player,45,0,8);
                flameThrowerShortRanged(player,-45,0,8);

                cooldown.put(uuid,10f);

            }else{
                player.sendMessage(org.bukkit.ChatColor.DARK_RED + "Your Cooldown is: " + (cooldown.get(uuid)/20));
                player.playSound(player,Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
//            flameThrowerShortRanged(player,45,20,8);
//            flameThrowerShortRanged(player,-45,20,8);

//            flameThrowerShortRanged(player,45,-20,8);
//            flameThrowerShortRanged(player,-45,-20,8);

        }
    }

    private void flameThrowerShortRanged(Player player,float rotation,float yrotation, float range){

        new BukkitRunnable(){

            Location location = player.getEyeLocation();
            {location.setYaw(location.getYaw()+rotation);}
            {location.setPitch(location.getPitch()+yrotation);}
            Vector direction = location.getDirection();
            float i = 0;
            @Override
            public void run(){

                if (i<=range){

                    Location current = location.clone().add(direction.clone().multiply(i));
                    int amountOfParticles = (int) (i*4);
                    current.getWorld().spawnParticle(Particle.FLAME,current,amountOfParticles,0.2*i/2,0.2*i/2,0.2*i/2,0);

                    for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                        if (!livingEntity.equals(player)){
                            damageNoTicks(livingEntity,21,player);
                        }
                    }

                    i+=0.3f;
                }else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin,0,1);

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
