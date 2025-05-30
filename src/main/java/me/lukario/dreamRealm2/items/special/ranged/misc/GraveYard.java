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

import java.util.*;

public class GraveYard implements Listener {

    private final Plugin plugin;

    public GraveYard(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "GraveYard";
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
    public void clawsUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cooldownLeft.get(uuid)==null){
            cooldownLeft.put(uuid,0f);
        }
        if (cooldownRight.get(uuid)==null){
            cooldownRight.put(uuid,0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction()==Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
            if (cooldownLeft.get(uuid)==0){

                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
                getLivingEntity(player,4);
                cooldownLeft.put(uuid,10f);

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownLeft.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
        if (event.getAction()==Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK){
            if (cooldownRight.get(uuid)==0){

                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
                getLivingEntity(player,8);
                cooldownRight.put(uuid,120f);

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownRight.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
    }

    private void missileLeftClicked(Player player, LivingEntity livingEntity,boolean bool){
        Location startLocation = player.getLocation().add(0,1,0);
        org.bukkit.util.Vector tempDirection = startLocation.getDirection().normalize();

        startLocation=startLocation.clone().add(tempDirection.clone().multiply(-1));

        if (random.nextBoolean()){
            startLocation.setYaw(startLocation.getYaw()+30+random.nextInt(61));
        }else{
            startLocation.setYaw(startLocation.getYaw()-30-random.nextInt(61));
        }

        startLocation.setPitch(startLocation.getPitch()-30-random.nextInt(61));

        org.bukkit.util.Vector direction = startLocation.getDirection().normalize();

        player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1,1);

        for (float i = 0; i <= 8; i+=0.5f){
            Location current = startLocation.clone().add(direction.clone().multiply(i));

            current.getWorld().spawnParticle(Particle.FIREWORK,current,1,0,0,0,0);
            if (i==0){
                current.getWorld().playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,2,1);
            }
            if (i==8){
                if (!bool){
//                    new BukkitRunnable(){@Override public void run(){twoPlaceRayCast(current,player,livingEntity,bool);this.cancel();}}.runTaskTimer(plugin,20,1);
                    twoPlaceRayCast(current,player,livingEntity,bool);
                }else{
                    twoPlaceRayCast(current,player,livingEntity,bool);
                }
            }
        }

    }

    private void twoPlaceRayCast(Location location,Player player, LivingEntity livingEntity, Boolean bool){
        Location startLocation = location;

        new BukkitRunnable(){
            float i =0;
            @Override
            public void run(){
                Location endLocation = livingEntity.getLocation();

                double distance = startLocation.distance(endLocation);
                org.bukkit.util.Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();


                Location current = startLocation.clone().add(direction.clone().multiply(i));

                current.getWorld().spawnParticle(Particle.FIREWORK,current,1,0,0,0,0);

                if (i>distance-1){
                    for (LivingEntity livingEntity1: current.getNearbyLivingEntities(3)){
                        if (!livingEntity1.equals(player)){
                        if (!livingEntity1.isDead()){
                            Misc.damageNoTicks(livingEntity1,32,player);
                        }
                            current.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,current,1,0,0,0,0);
                            current.getWorld().playSound(current,Sound.ENTITY_GENERIC_EXPLODE,3,1);
                        }
                    }
                    for (LivingEntity livingEntity1: current.getNearbyLivingEntities(2)){
                        if (!livingEntity1.equals(player)){
                        if (!livingEntity1.isDead()){
                            Misc.damageNoTicks(livingEntity1,32,player);
                        }
                        }
                    }
                    for (LivingEntity livingEntity1: current.getNearbyLivingEntities(1)){
                        if (!livingEntity1.equals(player)){
                        if (!livingEntity1.isDead()){
                            Misc.damageNoTicks(livingEntity1,32,player);
                        }
                        }
                    }
                    this.cancel();
                    return;
                }
                i+=1f;
            }
        }.runTaskTimer(plugin,0,1);
    }


    private void getLivingEntity(Player player, int radius) {
    Location eyeLocation = player.getEyeLocation();
    Vector direction = eyeLocation.getDirection().normalize();
    Location finalLocation = null;

    // Trace a path from the player's eye location up to 96 blocks away
    for (double i = 0; i <= 96; i += 0.5) {
        Location current = eyeLocation.clone().add(direction.clone().multiply(i));
        // Stop the ray if a non-air block is encountered
        if (current.getBlock().getType() != Material.AIR) {
            for (LivingEntity livingEntity : current.getNearbyLivingEntities(radius)) {
                if (!livingEntity.equals(player) && !livingEntity.isDead()) {

                   missileLeftClicked(player, livingEntity, false);
                   missileLeftClicked(player, livingEntity, false);
                   missileLeftClicked(player, livingEntity, false);
                   missileLeftClicked(player, livingEntity, false);
                }
            }
            finalLocation = current;
            break;
        }
        finalLocation = current;
    }

    // If a final location was determined, spawn particles around it in a circle
    if (finalLocation != null) {
        int numberOfPoints = 20;
        double angleStep = 360.0 / numberOfPoints;
        finalLocation.setYaw(0);
        finalLocation.setPitch(0);

        for (int i = 0; i < numberOfPoints; i++) {
            // Clone the final location for rotation
            Location particleLocation = finalLocation.clone();
            // Calculate the yaw offset for this point
            particleLocation.setYaw(particleLocation.getYaw() + (float) (angleStep * i));
            Vector spawnDirection = particleLocation.getDirection().normalize();
            Location spawnPoint = particleLocation.clone().add(spawnDirection.multiply(radius));

            spawnPoint.getWorld().spawnParticle(Particle.DUST, spawnPoint.add(0,0.5,0), 2, 0, 0, 0, new Particle.DustOptions(Color.RED, 2.0f));

        }
    }
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
