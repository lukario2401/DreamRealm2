package me.lukario.dreamRealm2.items.special.magic;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Theather implements Listener {

    boolean canUse = true;

    private final Plugin plugin;

    public Theather(Plugin plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, LivingEntity> targetedEntities = new HashMap<>();

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Theater";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ####";
    private static final Material ITEM_MATERIAL = Material.NETHER_STAR;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(16);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void theatherUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (!isHoldingTheCorrectItem(player)){return;}
        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){

            LivingEntity livingEntity = rayCastFindTheTargetedPlayer(player);
            targetedEntities.put(player.getUniqueId(), livingEntity);
            if (livingEntity!=null){
                canUse = false;
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        canUse=true;
                        twoPersonRayCast(player,livingEntity);
                        this.cancel();
                    }
                }.runTaskTimer(plugin,2,1);
            }
        }
        if (Action.LEFT_CLICK_AIR==event.getAction()||Action.LEFT_CLICK_BLOCK==event.getAction()){
            LivingEntity targetEntity = targetedEntities.get(player.getUniqueId());
            teleport(player,targetEntity);
        }
    }

    private void teleport(Player player,LivingEntity globalLivingEntity){
        if (globalLivingEntity!=null){

            Location teleportLocation = globalLivingEntity.getLocation().add(0,1,0);
            teleportLocation = teleportLocation.setDirection(player.getLocation().getDirection());

            player.teleport(teleportLocation);
            canUse = false;
            targetedEntities.put(player.getUniqueId(),null);
        }
    }

    private LivingEntity rayCastFindTheTargetedPlayer(Player player){

        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i = 0; i <= 24; i+=0.5f){
            Location currentLocation = location.clone().add(direction.clone().multiply(i));
            for (LivingEntity livingEntity : currentLocation.getNearbyLivingEntities(1.5)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
            }
        }
        return null;
    }

    private void twoPersonRayCast(Player player,LivingEntity livingEntity){
        new BukkitRunnable(){
            int runTime = 0;
            @Override
            public void run(){
                if (livingEntity.isDead()){
                    runTime+=400;
                }
                if (!canUse){
                    runTime+=400;
                }
                if (runTime>=400){
                    this.cancel();
                    targetedEntities.put(player.getUniqueId(),null);
                }

                Location startLocation = player.getLocation().add(0,1,0);
                Location endLocation = livingEntity.getLocation().add(0,1,0);
                Location current = startLocation.clone();

                Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();
                Double maxDistance = startLocation.distance(endLocation);

                for (float i =0; i < maxDistance * 2; i +=0.5f){

                    current.add(direction.clone().multiply(0.5));
                    current.getWorld().spawnParticle(Particle.ENCHANTED_HIT, current, 1, 0, 0, 0, 0);

                    if (current.distanceSquared(startLocation) > maxDistance * maxDistance) {
                        break;
                    }
                }

                runTime+=1;
            }
        }.runTaskTimer(plugin,0,1L);
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
