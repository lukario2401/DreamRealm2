package me.lukario.dreamRealm2.items.swords;

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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Claws implements Listener {
    private final Plugin plugin;

    public Claws(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Claws";
    private static final String ITEM_LORE = ChatColor.YELLOW + "#########";
    private static final Material ITEM_MATERIAL = Material.WOODEN_AXE;

    private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();
    private static final HashMap<UUID,Float> cooldownRight = new HashMap<>();
    private static final HashMap<UUID,Boolean> isUsingTheAbility = new HashMap<>();

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
    public void itemDisplayClaw(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (isUsingTheAbility.get(uuid)==null){
            isUsingTheAbility.put(uuid,false);
        }
        if (!isHoldingTheCorrectItem(player)){
            isUsingTheAbility.put(uuid,false);
        }else{
            isUsingTheAbility.put(uuid,false);
        }
        if (isUsingTheAbility.get(uuid)==false){
            displayClaw(player);
        }
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

                LivingEntity livingEntity = getLivingEntity(player);

                if (livingEntity!=null){
                    clawLeftClicked(player,livingEntity);
                    player.playSound(player,Sound.ENTITY_BLAZE_SHOOT,1,1);
                    cooldownLeft.put(uuid,10f);
                }

            }else{
                player.sendMessage(ChatColor.DARK_RED+"Cooldown: "+(cooldownLeft.get(uuid)/20)+"s");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }

    }

    private void displayClaw(Player player){
        new BukkitRunnable(){
            @Override
            public void run(){
                if (isUsingTheAbility.get(player.getUniqueId())==true){
                    Location location = player.getLocation().add(0,1,0);
                    Vector direction = location.getDirection();

                    for (float i =0; i <= 1;i+=0.1f){
                        Location current = location.clone().add(direction.clone().multiply(i));

                        Vector rightOffset = direction.clone().crossProduct(new Vector(0, -0.5, 0)).normalize().multiply(i+0.2);

                        current = current.clone().add(rightOffset);

                        current.getWorld().spawnParticle(Particle.FLAME,current,1,0,0,0,0);
                    }
                }else{
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

    private LivingEntity getLivingEntity(Player player){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i = 0; i<=64; i+=0.5f) {
            Location current = location.clone().add(direction.clone().multiply(i));
            for (LivingEntity livingEntity : current.getNearbyLivingEntities((i/32)+0.5)) {
                if (!livingEntity.equals(player)) {
                    if (!livingEntity.isDead()){
                        return livingEntity;
                    }
                }
            }
        }
        return null;
    }

    private void clawLeftClicked(Player player, LivingEntity livingEntity){
        Location location = player.getLocation().add(0,1,0);
        Vector direction = location.getDirection();

        Location globalCurrent=location;

        for (float i =0; i <= 1;i+=0.1f){
            Location current = location.clone().add(direction.clone().multiply(i));

            Vector rightOffset = direction.clone().crossProduct(new Vector(0, -0.5, 0)).normalize().multiply(i+0.2);

            current = current.clone().add(rightOffset);

            current.getWorld().spawnParticle(Particle.FLAME,current,1,0,0,0,0);
            globalCurrent=current;
        }

        Location startLocation = globalCurrent.clone();
        Location endLocation = livingEntity.getLocation().add(0,1,0);

        double distance = startLocation.distance(endLocation);
        Vector directionOfTwoPlaceRayCast = endLocation.toVector().subtract(startLocation.toVector()).normalize();

        for (float j = 0; j <= distance; j+=0.1f){
            Location currentLocationOfTwoPlaceRayCast = startLocation.clone().add(directionOfTwoPlaceRayCast.clone().multiply(j));

            currentLocationOfTwoPlaceRayCast.getWorld().spawnParticle(Particle.FLAME,currentLocationOfTwoPlaceRayCast,1,0,0,0,0);

            if (j>(distance-0.1)){
                for (LivingEntity livingEntity1 : currentLocationOfTwoPlaceRayCast.getNearbyLivingEntities(3)){
                    if (!livingEntity1.equals(player)){
                        Misc.damageNoTicks(livingEntity1,52,player);
                    }
                }
            }
            for (LivingEntity livingEntity1 : currentLocationOfTwoPlaceRayCast.getNearbyLivingEntities(0.2)){
                if (!livingEntity1.equals(player)){
                    Misc.damageNoTicks(livingEntity1,52,player);
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
