package me.lukario.dreamRealm2.items.special.ranged.misc;

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

public class Swift implements Listener {

    private final Plugin plugin;

    public Swift(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Swift";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ####";
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
    public void linkUsed(PlayerInteractEvent event) {
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
                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

                LivingEntity livingEntity = getLivingEntity(player,96);

                if (livingEntity!=null){

                    twoPlaceRayCast(player.getEyeLocation(),livingEntity);

                }
                cooldown.put(uuid, 10f);
            } else {
                player.sendMessage(org.bukkit.ChatColor.DARK_RED + "Your Cooldown is: " + (cooldown.get(uuid) / 20) + " Seconds");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        }
    }

    private void twoPlaceRayCast(Location startLocationn, LivingEntity livingEntity){

    // Adjust the starting and target locations upward for eye-level
        Location adjustedStart = startLocationn.clone();
        Location adjustedTarget = livingEntity.getLocation().clone();

        // Calculate the distance from caster to target
        double distance = adjustedStart.distance(adjustedTarget);

        // Create a temporary location to get the rotated direction:
        Location tempLocation = adjustedStart.clone();
        tempLocation.setYaw(tempLocation.getYaw() + 35); // rotate 45° to the right
        tempLocation.setPitch(tempLocation.getPitch() - 35); // rotate 45° to the right
        Vector rayDirection = tempLocation.getDirection().normalize();

        // Cast the ray halfway to the target, spawning particles along the path
        Location globalCurrentLocation = startLocationn.clone();
        for (double d = 0; d <= distance / 1.5; d += 0.5) {
            Location currentLocation = adjustedStart.clone().add(rayDirection.clone().multiply(d));
            currentLocation.getWorld().spawnParticle(Particle.DUST, currentLocation, 10, 0, 0, 0,
                new Particle.DustOptions(Color.WHITE, 1.0f));
            globalCurrentLocation=currentLocation;
        }

        Location startLocation = globalCurrentLocation;
        Location endLocation = livingEntity.getLocation().add(0,1,0);

        double distanceForSecond = startLocation.distance(endLocation);
        Vector directionForSecond = endLocation.toVector().subtract(startLocation.toVector()).normalize();

        for (double i = 0; i <= distanceForSecond; i += 0.5) {

            Location currentLocation = globalCurrentLocation.clone().add(directionForSecond.clone().multiply(i));

            for (LivingEntity livingEntity1 : currentLocation.getNearbyLivingEntities(1)) {
                livingEntity1.damage(21);
            }

            currentLocation.getWorld().spawnParticle(Particle.DUST, currentLocation, 10, 0, 0, 0,
                    new Particle.DustOptions(Color.RED, 1.0f));

        }
    }

    private LivingEntity getLivingEntity(Player player,float range){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i =0; i <= 32; i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
            }
        }
        if(range>32){
            for (float i =0; i <= range; i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(2)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
            }
        }
        }
        if(range>64){
            for (float i =0; i <= range; i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            if (i>64){
            for (LivingEntity livingEntity : current.getNearbyLivingEntities(3)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
            }
            }else{
                for (LivingEntity livingEntity : current.getNearbyLivingEntities(2)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
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
