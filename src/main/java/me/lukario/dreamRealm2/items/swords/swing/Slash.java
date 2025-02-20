package me.lukario.dreamRealm2.items.swords.swing;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
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

public class Slash implements Listener {

     private final Plugin plugin;

    public Slash(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Slash";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating #####";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

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

    private static final HashMap<UUID,Float> cooldown = new HashMap<>();

    private void cooldownManagement(){
        new BukkitRunnable(){
            @Override
            public void run(){

                Set<UUID> set = cooldown.keySet();

                for (UUID uuid : set){
                    if(cooldown.get(uuid)>0){
                        cooldown.put(uuid,cooldown.get(uuid)-1);
                    }
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

    @EventHandler
    public void slashUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cooldown.get(uuid)==null){
            cooldown.put(uuid,0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;}

        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){

            player.sendMessage("Skill triggered");


        }
        if (Action.LEFT_CLICK_AIR==event.getAction()||Action.LEFT_CLICK_BLOCK==event.getAction()){
            if (cooldown.get(uuid)==0f){
                cooldown.put(uuid,10f);
                slash(player,0);
                slash(player,20);
            }else{
                player.sendMessage(ChatColor.DARK_RED + "Cooldown: " + cooldown.get(uuid)/20);
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }

    }

    private void slash(Player player,float Rotation) {

        Location rotatedLocation = player.getEyeLocation();
        Location location = rotatedLocation.clone();
        location.setYaw(location.getYaw() + Rotation);

        Vector direction = location.getDirection().normalize();

        Vector rightOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.5); // 0.5 blocks right
        Vector leftOffset = rightOffset.clone().multiply(-1);
        Vector leftOffsett = direction.clone().crossProduct(new Vector(0,1,0)).normalize().multiply(-0.5);

        for (float i = 0; i <= 6; i += 0.5f) {
            Location current = location.clone().add(direction.clone().multiply(i));

            current.getWorld().spawnParticle(Particle.CRIT, current, 1, 0, 0, 0, 0);
//            current.getWorld().spawnParticle(Particle.CRIT, current.clone().add(rightOffset), 1, 0, 0, 0, 0);
//            current.getWorld().spawnParticle(Particle.CRIT, current.clone().add(leftOffset), 1, 0, 0, 0, 0);

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)) {
                if (!livingEntity.equals(player)) {
                    livingEntity.damage(52, player);
                    livingEntity.getWorld().playSound(current, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                    livingEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK,current,1,0,0,0,0);
                    i+=6;
                }
            }
//            for (LivingEntity livingEntity : current.clone().add(rightOffset).getNearbyLivingEntities(1)) {
//                if (!livingEntity.equals(player)) {
//                    livingEntity.damage(52, player);
//                    livingEntity.getWorld().playSound(current.clone().add(rightOffset), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
//                    livingEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK,current.clone().add(rightOffset),1,0,0,0,0);
//                    i+=6;
//                }
//            }
//            for (LivingEntity livingEntity : current.clone().add(leftOffset).getNearbyLivingEntities(1)) {
//                if (!livingEntity.equals(player)) {
//                    livingEntity.damage(52, player);
//                    livingEntity.getWorld().playSound(current.clone().add(leftOffset), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
//                    livingEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK,current.clone().add(leftOffset),1,0,0,0,0);
//                    i+=6;
//                }
//            }
        }
    }

private void createSlash(Location current, float rotation, int customModelData) {
    // Offset the slash sideways based on rotation
    Vector offset = new Vector(
        -Math.sin(Math.toRadians(current.getYaw() + rotation)),
        0,
        Math.cos(Math.toRadians(current.getYaw() + rotation))
    ).multiply(0.5);

    Location spawnLocation = current.clone().add(offset);

    ItemStack displayItem = new ItemStack(Material.FEATHER);
    ItemMeta meta = displayItem.getItemMeta();
    meta.setCustomModelData(customModelData);
    displayItem.setItemMeta(meta);

    ItemDisplay display = (ItemDisplay) current.getWorld().spawnEntity(
        spawnLocation,
        EntityType.ITEM_DISPLAY
    );
    display.setItemStack(displayItem);
    display.setRotation(current.getYaw() + rotation, current.getPitch());

    new BukkitRunnable() {
        @Override
        public void run() {
            display.remove();
        }
    }.runTaskLater(plugin, 5);
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
