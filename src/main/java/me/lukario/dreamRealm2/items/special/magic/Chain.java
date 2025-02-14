package me.lukario.dreamRealm2.items.special.magic;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Chain implements Listener {

    private final Plugin plugin;

    public Chain(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Chain";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ####";
    private static final Material ITEM_MATERIAL = Material.PAPER;

    private static final HashMap<UUID,Float> cooldown = new HashMap<>();

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

    private void cooldownManagement(){
        new BukkitRunnable(){
            @Override
            public void run(){
                Set<UUID> keySet =  cooldown.keySet();
                for (UUID uuid : keySet){
                    if (cooldown.get(uuid)>0){
                        cooldown.put(uuid,cooldown.get(uuid)-1f);
                    }
                }

            }
        }.runTaskTimer(plugin,0,1);
    }

    @EventHandler
    public void chainUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (cooldown.get(player.getUniqueId())==null){
            cooldown.put(player.getUniqueId(),0F);
            player.sendMessage("triggerted");
        }

        if (!isHoldingTheCorrectItem(player)){return;}
        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){

            if (cooldown.get(player.getUniqueId())==0){
                LivingEntity livingEntity = chainGetMob(player);
                if (livingEntity==null){
                    player.sendMessage("gay");
                }else{
                    beamBetweenTwoEntity(player,livingEntity);
                }
                cooldown.put(player.getUniqueId(),200f);
            }else{
                player.sendMessage("Cooldown is: " + Math.round(cooldown.get(player.getUniqueId())/20));
                player.getWorld().playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }

        }
    }

    private void beamBetweenTwoEntity(Player player, LivingEntity livingEntity){
        new BukkitRunnable(){
            int timeAlive = 0;
            @Override
            public void run(){
                if (livingEntity.isDead()||player.isDead()){
                    timeAlive+=200;
                }
                if (timeAlive>=200){
                    this.cancel();
                }
                Location startLocation = player.getLocation().add(0,1,0);
                Location endLocation = livingEntity.getLocation().add(0,1,0);

                Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();
                double distance = startLocation.distance(endLocation);
                Location current = startLocation;

                for (float i =0; i <= distance; i += 0.5f){

                    current.getWorld().spawnParticle(Particle.ENCHANTED_HIT,current,1,0,0,0,0);
                    current.add(direction.clone().multiply(0.5));

                }
                if (timeAlive%20==0){
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,30,255,true));
                }
                timeAlive+=1;
            }
        }.runTaskTimer(plugin,0,1);
    }

    private LivingEntity chainGetMob(Player player){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();
        Location current = location;

        for (float i =0; i <= 32; i+=0.5f){

            current.getWorld().spawnParticle(Particle.SOUL,current,1,0,0,0,0);

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
            }

            current = location.clone().add(direction.clone().multiply(i));
        }
        return null;
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
