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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Satellite implements Listener {

    private final Plugin plugin;

    public Satellite(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Satellite";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating #####";
    private static final Material ITEM_MATERIAL = Material.GOLDEN_SHOVEL;

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

    private static HashMap<UUID, Float> cooldown = new HashMap<>();

    private void cooldownManagement() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<UUID> key = cooldown.keySet();

                for (UUID uuid : key) {
                    if (cooldown.get(uuid) > 0) {
                        cooldown.put(uuid, cooldown.get(uuid) - 1);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler
    public void satelliteUsed(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cooldown.get(uuid) == null) {
            cooldown.put(uuid, 0f);
        }

        if (!isHoldingTheCorrectItem(player)) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (cooldown.get(uuid) == 0) {

                LivingEntity livingEntity = getLivingEntity(player);

                if (livingEntity!=null){

                    player.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

                    Location satelliteLocation = launchSatellite(player);
                    if (satelliteLocation!=null){
                        satelliteAttack(satelliteLocation,livingEntity);
                    }

                }
                cooldown.put(uuid, 2f);
            } else {
                player.sendMessage(org.bukkit.ChatColor.DARK_RED + "Your Cooldown is: " + (cooldown.get(uuid) / 20) + " Seconds");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        }
    }

    private void satelliteAttack(Location satelliteLocation,LivingEntity livingEntity){
        Location startLocation = satelliteLocation;

        new BukkitRunnable(){
            int timeRunning=0;
            @Override public void run(){
                if (timeRunning>=60){
                    this.cancel();
                }

                satelliteLocation.getWorld().spawnParticle(Particle.FLAME,satelliteLocation,10,0.2,0.2,0.2,0);
                satelliteLocation.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,satelliteLocation,20,0.1,0.1,0.1,0);

                timeRunning+=1;
            }
        }.runTaskTimer(plugin,0,1);

        new BukkitRunnable(){
            float i = 0;
            @Override
            public void run(){
                Location endLocation = livingEntity.getLocation();

                double distance = startLocation.distance(endLocation);
                Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();

                if (i>=distance){
                    this.cancel();
                }

                Location current = satelliteLocation.clone().add(direction.clone().multiply(i));

                current.getWorld().spawnParticle(Particle.FLAME,current,1,0,0,0,0);
                if (i>=distance-0.5){
                    for (LivingEntity livingEntity1 : current.getNearbyLivingEntities(2)){
                        livingEntity1.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,livingEntity1.getLocation(),1);
                        livingEntity1.getWorld().playSound(livingEntity.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,5,1);
                        Misc.damageNoTicks(livingEntity1,120);
                    }
                }
                if (i>distance/2){
                    current = satelliteLocation.clone().add(direction.clone().multiply(i));
                    current.getWorld().spawnParticle(Particle.FLAME,current,1,0,0,0,0);
                    if (i>=distance-0.5){
                        for (LivingEntity livingEntity1 : current.getNearbyLivingEntities(3)){
                            livingEntity1.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,livingEntity1.getLocation(),1);
                            livingEntity1.getWorld().playSound(livingEntity.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,5,1);
                            Misc.damageNoTicks(livingEntity1,120);
                        }
                    }
                    i+=0.5f;
                }

                i+=0.5f;
            }
        }.runTaskTimer(plugin,60,1);
    }

    private Location launchSatellite(Player player){

        Location location = player.getEyeLocation();
        location.setPitch(-80);
        Vector direction = location.getDirection().normalize();

        for (float i =0; i<=24;i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            current.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,current,1,0,0,0,0);

            if (i==24){
                return current;
            }
        }
        return null;
    }

    private LivingEntity getLivingEntity(Player player){

        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i =0; i<=96; i+=0.5f){
            if (i<32){
                Location current = location.clone().add(direction.clone().multiply(i));
                for (LivingEntity livingEntity:current.getNearbyLivingEntities(1)){
                    if (!livingEntity.equals(player)){
                        return livingEntity;
                    }
                }
            }
            if (i>32){
                Location current = location.clone().add(direction.clone().multiply(i));
                for (LivingEntity livingEntity:current.getNearbyLivingEntities(2)){
                    if (!livingEntity.equals(player)){
                        return livingEntity;
                    }
                }
            }
            if (i>64){
                Location current = location.clone().add(direction.clone().multiply(i));
                for (LivingEntity livingEntity:current.getNearbyLivingEntities(3)){
                    if (!livingEntity.equals(player)){
                        return livingEntity;
                    }
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
