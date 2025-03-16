package me.lukario.dreamRealm2.items.special.ranged.misc;


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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class FireCracker implements Listener {

private final Plugin plugin;

    public FireCracker(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "FireCracker";
    private static final String ITEM_LORE = ChatColor.YELLOW + "########";
    private static final Material ITEM_MATERIAL = Material.IRON_SHOVEL;

    private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();
    private static final HashMap<UUID,Float> cooldownRight = new HashMap<>();

    Random random = new Random();

    private void cooldownManagement(){
        new BukkitRunnable(){
            @Override
            public void run(){
                Set<UUID> key = cooldownLeft.keySet();
                for (UUID uuid : key){
                    if (cooldownLeft.get(uuid)>0){
                        cooldownLeft.put(uuid,cooldownLeft.get(uuid)-1);
                    }
                }
            }
        }.runTaskTimer(plugin,0,1);
        new BukkitRunnable(){
            @Override
            public void run(){
                Set<UUID> key = cooldownRight.keySet();
                for (UUID uuid : key){
                    if (cooldownRight.get(uuid)>0){
                        cooldownRight.put(uuid,cooldownRight.get(uuid)-1);
                    }
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

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
    public void sphereCageUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cooldownLeft.get(uuid)==null){
            cooldownLeft.put(uuid,0f);
        }
        if (cooldownRight.get(uuid)==null){
            cooldownRight.put(uuid,0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
            if (cooldownLeft.get(uuid)==0){

                if (getLivingEntity(player)!=null){
                    fireCrackerUsed(player,getLivingEntity(player));
                    player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
                }

                cooldownLeft.put(uuid,2f);

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownLeft.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
    }

    private void fireCrackerUsed(Player player, LivingEntity livingEntityus){
        if (livingEntityus!=null){
            Location location = livingEntityus.getLocation().add(0,1,0);
            location.setDirection(player.getLocation().getDirection());

            location.setPitch(0);

            for (float j = -45f; j <= 45; j+=9){

                Location rotatedLocation = location.clone();
                rotatedLocation.setYaw(location.getYaw()+j);

                Vector direction = rotatedLocation.getDirection().normalize();

                for (float i =0; i < 6; i +=1f){

                    Location current = rotatedLocation.clone().add(direction.clone().multiply(i));
                    current.getWorld().spawnParticle(Particle.FIREWORK,current,1,0,0,0,0);

                    for(LivingEntity livingEntity : current.getNearbyLivingEntities(0.4f)){
                        if (!livingEntity.equals(livingEntityus)){
                            if (livingEntity!=livingEntityus){
                                if (!livingEntity.isDead()){
                                    fireCrackerUsed(player,livingEntity);
                                }
                            }
                        }
                    }
                }
            }
            Misc.damageNoTicks(livingEntityus,52,player);
        }
    }

    private LivingEntity getLivingEntity(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        for (float i = 0; i <= 96; i+=1) {
            Location current = eyeLocation.clone().add(direction.clone().multiply(i));
            current.getWorld().spawnParticle(Particle.FIREWORK,current,1,0,0,0,0);
                for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)) {
                    if (!livingEntity.equals(player) && !livingEntity.isDead()) {

                        return livingEntity;

                    }
                }
        }
        return null;
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
