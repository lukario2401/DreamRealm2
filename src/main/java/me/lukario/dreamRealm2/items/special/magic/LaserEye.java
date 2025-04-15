package me.lukario.dreamRealm2.items.special.magic;

import me.lukario.dreamRealm2.Misc;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class LaserEye implements Listener {

    private final Plugin plugin;

    public LaserEye(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Laser Eye";
    private static final String ITEM_LORE = ChatColor.YELLOW + "##########";
    private static final Material ITEM_MATERIAL = Material.IRON_SHOVEL;

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
    public void usedLaserEye(PlayerInteractEvent event){

        Player player = event.getPlayer();
        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction() == Action.RIGHT_CLICK_AIR||event.getAction() == Action.RIGHT_CLICK_BLOCK){
            LivingEntity livingEntity = getEntity(player);
            if (livingEntity!=null){
                laserEyeHit(livingEntity,player);
            }else{
                player.sendMessage("Invalid Entity");
            }
        }
    }

    private LivingEntity getEntity(Player player){

        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i = 0; i < 96; i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            current.getWorld().spawnParticle(Particle.SOUL,current,1,0,0,0,0);

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(0.5+(96/24))){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
            }
        }
        return null;
    }

    private void laserEyeHit(LivingEntity livingEntity, Player player){
        new BukkitRunnable(){
            float runningTime = 0;
            @Override
            public void run(){
                if (livingEntity.isDead()){
                    runningTime+=256;
                }
                if (runningTime>=12){
                    this.cancel();
                }

                Location location = livingEntity.getLocation().add(0,1,0);

                location.getWorld().spawnParticle(Particle.DUST,location,10,0.2,0.5,0.2, new Particle.DustOptions(Color.GREEN,1));
                location.getWorld().spawnParticle(Particle.DUST,location,5,0.2,0.5,0.2, new Particle.DustOptions(Color.GREEN,2));
                livingEntity.playHurtAnimation(1);

                location.getWorld().playSound(location, Objects.requireNonNull(livingEntity.getHurtSound()),1,1);

                Misc.damageNoTicks(livingEntity,5, player);
                runningTime+=1;
            }
        }.runTaskTimer(plugin,0,20);
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
