package me.lukario.dreamRealm2.items.special.ranged.bow;

import me.lukario.dreamRealm2.Misc;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Terminator implements Listener {

    private final Plugin plugin;

    public Terminator(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Terminator";
    private static final String ITEM_LORE = ChatColor.YELLOW + "#######################";
    private static final Material ITEM_MATERIAL = Material.STONE_HOE;

    private static final HashMap<UUID,Float> cooldown = new HashMap<>();
    private static final HashMap<UUID,Float> cooldownRight = new HashMap<>();

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
    public void terminatorUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!isHoldingTheCorrectItem(player)){return;}
        if (cooldown.get(uuid)==null){
            cooldown.put(uuid,0f);
        }
        if (cooldownRight.get(uuid)==null){
            cooldownRight.put(uuid,0f);
        }

        if (Action.LEFT_CLICK_AIR==event.getAction()||Action.LEFT_CLICK_BLOCK==event.getAction()){
            if (cooldown.get(uuid)==0){
                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);

                cooldown.put(uuid,10f);
                shootTerminatorLeftCLick(player);

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+cooldown.get(uuid)/20+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }

        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){
            if (cooldownRight.get(uuid)==0){

                LivingEntity livingEntity = getRayCastHit(player);
                if (livingEntity!=null) {
                    cooldownRight.put(uuid,20f);
                    shootTerminatorRightCLick(player,livingEntity);
                    player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);
                }
            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+cooldownRight.get(uuid)/20+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }

    }

    private LivingEntity getRayCastHit(Player player){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i = 0; i <= 64; i += 1){
            Location current = location.clone().add(direction.clone().multiply(i));
            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
            }
        }
        return null;
    }

    private void shootTerminatorRightCLick(Player player,LivingEntity livingEntity){
        for (float i = 0; i <= 7; i+=1f){
            randomPointCreator(player,livingEntity);
        }
    }

    private void randomPointCreator(Player player,LivingEntity livingEntity){

        int x = (int) (Math.random() * 9) - 4;
        int y = (int) (Math.random() * 6) + 2;
        int z = (int) (Math.random() * 9) - 4;


        Location startLocation = player.getLocation().add(x,y,z);
//        Location endLocation = livingEntity.getLocation().add(0,1,0);

        circleCreate(livingEntity,startLocation,player);
    }

    private void circleCreate(LivingEntity livingEntityLocation, Location location1,Player player){
        new BukkitRunnable(){
            int timeRunning = 0;
            @Override
            public void run(){
                if (timeRunning>=60){
                    this.cancel();
                }
                location1.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,location1,5,0.1,0.1,0.1,0);

                location1.getWorld().spawnParticle(Particle.FLAME,location1,5,0,0.5,0,0);
                location1.getWorld().spawnParticle(Particle.FLAME,location1,5,0.5,0,0,0);
                location1.getWorld().spawnParticle(Particle.FLAME,location1,5,0,0,0.5,0);

                if (timeRunning==0){
                    location1.getWorld().playSound(location1,Sound.BLOCK_BEACON_ACTIVATE,3,1);
                }
                if (timeRunning==30){
                    location1.getWorld().playSound(location1,Sound.BLOCK_BEACON_POWER_SELECT,3,1);
                }
                if (timeRunning==61){
                    location1.getWorld().playSound(location1,Sound.BLOCK_BEACON_POWER_SELECT,3,1);
                }

                timeRunning+=1;
            }
        }.runTaskTimer(plugin,0,1);

        new BukkitRunnable(){
            int timeRunning = 0;
            @Override
            public void run(){
                if (timeRunning>=1){
                    this.cancel();
                }

                Location startLocation = livingEntityLocation.getLocation().add(0,1,0);
                Location endLocation = location1;

                double distance = startLocation.distance(endLocation);
                Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();

                for (float i =0; i<=distance; i+=0.5f){
                    Location current = startLocation.clone().add(direction.clone().multiply(i));
                    current.getWorld().spawnParticle(Particle.SOUL,current,1,0,0,0,0);

                    for (LivingEntity livingEntity : current.getNearbyLivingEntities(3)){
                        if (!livingEntity.equals(player)){
                            livingEntity.getWorld().playSound(livingEntity.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,5,1);
                            livingEntity.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,livingEntity.getLocation(),1,0,0,0,0);

                            for (LivingEntity livingEntity1 : livingEntity.getLocation().getNearbyLivingEntities(3)){
                                if (!livingEntity1.equals(player)){
                                    Misc.damageNoTicks(livingEntity1,52,player);
                                }
                            }

                            Misc.damageNoTicks(livingEntity,52);
                        }
                    }
                }

                timeRunning+=1;
            }
        }.runTaskTimer(plugin,60,1);
    }

    private void shootTerminatorLeftCLick(Player player){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i =0; i<=64;i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            if (current.getBlock().getType()!=Material.AIR){
                i+=65f;
            }

            current.getWorld().spawnParticle(Particle.FLAME,current,1,0,0,0,0);

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1.5)){
                if (!livingEntity.equals(player)){

                    for (LivingEntity livingEntity1 : current.getNearbyLivingEntities(8)){
                        if (!livingEntity1.equals(player)){
                            if (!livingEntity1.equals(livingEntity)){
                                rayCastBetweenTwoLivingEntities(livingEntity,livingEntity1,player);
                            }
                        }
                    }

                    Misc.damageNoTicks(livingEntity,52,player);
                    return;
                }
            }
        }
    }

    private void rayCastBetweenTwoLivingEntities(LivingEntity livingEntity, LivingEntity livingEntity1,Player player){
        Location startLocation = livingEntity.getLocation().add(0,1,0);
        Location endLocation = livingEntity1.getLocation().add(0,1,0);

        double distance = startLocation.distance(endLocation);
        Vector directionOfShatter = endLocation.toVector().subtract(startLocation.toVector()).normalize();

        for (float j = 0; j <= distance; j+=0.5f){

            Location currentLocationOfShatter = startLocation.clone().add(directionOfShatter.clone().multiply(j));
            currentLocationOfShatter.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,currentLocationOfShatter,1,0,0,0,0);

            for (LivingEntity livingEntity2 : currentLocationOfShatter.getNearbyLivingEntities(1)){
                if (!livingEntity2.equals(player)){

                    Misc.damageNoTicks(livingEntity2,52,player);
                }
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
