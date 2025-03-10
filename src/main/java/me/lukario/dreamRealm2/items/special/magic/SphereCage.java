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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SphereCage implements Listener {
    private final Plugin plugin;

    public SphereCage(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "SphereCage";
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
                    sphereCageLeftClicked(Objects.requireNonNull(getLivingEntity(player)));
                    player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
                }

                cooldownLeft.put(uuid,2f);

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownLeft.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
        if (event.getAction()==Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK){
            if (cooldownRight.get(uuid)==0){

                if (getLivingEntity(player)!=null){
                    sphereCageRightClicked(getLivingEntity(player));
                    player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
                }
                cooldownRight.put(uuid,120f);

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownRight.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
    }

    private void sphereCageLeftClicked(LivingEntity livingEntity){
        Location location = livingEntity.getLocation().add(0,1,0);

        new BukkitRunnable(){
            int timeRunning = 0;
            @Override
            public void run(){
                if (timeRunning>40){
                    this.cancel();
                }

                for (LivingEntity livingEntity1 : location.getNearbyLivingEntities(4)){
                    if (!livingEntity.isDead()){
                        livingEntity1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,10,155));
                        livingEntity1.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,10,155));
                        livingEntity1.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,10,155));
                    }
                }

                yAxis(location,20, 0,4 );
                yAxis(location,20, 45, 4);
                yAxis(location,20, -45, 4);

                xzAxis(location,20, 0, 4);
                xzAxis(location,20, 45, 4);
                xzAxis(location,20, 90, 4);
                xzAxis(location,20, 135f, 4);

                timeRunning++;
            }
        }.runTaskTimer(plugin,0,1);
    }

    private void sphereCageRightClicked(LivingEntity livingEntity){
        Location location = livingEntity.getLocation().add(0,1,0);

        new BukkitRunnable(){
            int timeRunning = 0;
            @Override
            public void run(){
                if (timeRunning>60){
                    this.cancel();
                }
                for (LivingEntity livingEntity1 : location.getNearbyLivingEntities(5.5)){
                    if (!livingEntity.isDead()){
                        livingEntity1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,10,155));
                        livingEntity1.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,10,155));
                        livingEntity1.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,10,155));
                    }
                }

                yAxis(location,20, 0, 6);
                yAxis(location,20, 22.5f, 6);
                yAxis(location,20, 45, 6);
                yAxis(location,20, 67.5f, 6);
                yAxis(location,20, -22.5f, 6);
                yAxis(location,20, -45, 6);
                yAxis(location,20, -67.5f, 6);

                xzAxis(location,20, 0, 6);
                xzAxis(location,20, 22.5f, 6);
                xzAxis(location,20, 45, 6);
                xzAxis(location,20, 67.5f, 6);
                xzAxis(location,20, 90, 6);
                xzAxis(location,20, 112.5f, 6);
                xzAxis(location,20, 135f, 6);
                xzAxis(location,20, 157.5f, 6);
                xzAxis(location,20, 180f, 6);

                timeRunning++;
            }
        }.runTaskTimer(plugin,0,1);
    }

    private void yAxis(Location location, int points, float pitch, float radius){

        location.setYaw(0);
        location.setPitch(pitch);

        for (float i = 0; i <= points; i+=1){

            location.setYaw(location.getYaw()+((float) 360 /points));

            Vector direction = location.getDirection().normalize();
            Location current = location.clone().add(direction.clone().multiply(radius));

            current.getWorld().spawnParticle(Particle.DUST,current,1,0,0,0,new Particle.DustOptions(Color.RED,2));
        }
    }

    private void xzAxis(Location location, int points, float yaw, float radius){

        location.setYaw(yaw);
        location.setPitch(0);

        for (float i = 0; i <= points; i+=1){

            location.setPitch(location.getPitch()+((float) 360 /points));

            Vector direction = location.getDirection().normalize();
            Location current = location.clone().add(direction.clone().multiply(radius));

            current.getWorld().spawnParticle(Particle.DUST,current,1,0,0,0,new Particle.DustOptions(Color.RED,2));

        }
    }

    private LivingEntity getLivingEntity(Player player) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        for (float i = 0; i <= 96; i+=1) {
            Location current = eyeLocation.clone().add(direction.clone().multiply(i));
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
