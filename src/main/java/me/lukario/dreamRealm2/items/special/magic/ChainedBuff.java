package me.lukario.dreamRealm2.items.special.magic;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ChainedBuff implements Listener {

    private final Plugin plugin;

    public ChainedBuff(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Chained Buff";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating #####";
    private static final Material ITEM_MATERIAL = Material.DIAMOND;

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
            @Override public void run(){
                Set<UUID> uuid = cooldown.keySet();

                for (UUID uuid1 : uuid){
                    if (cooldown.get(uuid1)>0f){
                        cooldown.put(uuid1,cooldown.get(uuid1)-1f);
                    }
                }
            }
        }.runTaskTimer(plugin,0,1);



    }

    private static final HashMap<UUID,Float> cooldown = new HashMap<>();


    @EventHandler
    public void chainedBuffUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if (cooldown.get(player.getUniqueId())==null){
            cooldown.put(player.getUniqueId(),0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;}

        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){
            if (cooldown.get(player.getUniqueId())==0){
                cooldown.put(player.getUniqueId(),200f);
    //            createSlash(player,45f);
    //            createSlash(player,-45f);

                LivingEntity livingEntity = getLivingEntity(player);
                if (livingEntity!=null){
                    twoPlaceBeam(player,livingEntity);
                }
            }else{
                player.sendMessage(ChatColor.DARK_RED + "Your cooldown is: " + Math.round(cooldown.get(player.getUniqueId())/20));
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }


        }

    }

    private LivingEntity getLivingEntity(Player player){

        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();

        for (float i = 0; i <= 32; i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
                }
            }

            current.getWorld().spawnParticle(Particle.CRIT,current,1,0,0,0,0);
        }

        return null;
    }

    private void twoPlaceBeam(Player player,LivingEntity livingEntity){

        new BukkitRunnable(){
            int aliveTime = 0;
            @Override
            public void run(){
                if (player.isDead()||livingEntity.isDead()){
                    aliveTime+=200;
                }
                if (aliveTime>=200){
                    this.cancel();
                }
                Location startLocation = player.getLocation().add(0,1,0);
                Location endLocation = livingEntity.getLocation().add(0,1,0);

                Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();
                double distance = startLocation.distance(endLocation);

                Location current = startLocation;

                for (float i =0; i <= distance; i+=0.5f){

                    current.getWorld().spawnParticle(Particle.CRIT,current,1,0,0,0,0);

                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,10,1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,10,1));

                    current.add(direction.clone().multiply(0.5));
                }
                aliveTime+=0.5;
            }
        }.runTaskTimer(plugin,0,1);
    }

    private void createSlash(Player player,float rotation){
        ItemStack displayItem = new ItemStack(Material.FEATHER);
            ItemMeta meta = displayItem.getItemMeta();
            meta.setCustomModelData(1); // Match your resource pack
            displayItem.setItemMeta(meta);

            // Spawn the ItemDisplay entity
            Location spawnLocation = player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection().normalize().multiply(1));

            ItemDisplay display = (ItemDisplay) player.getWorld().spawnEntity(
                spawnLocation,
                EntityType.ITEM_DISPLAY
            );
            display.setItemStack(displayItem);
//            display.setRotation(90, 0); // Rotate to look like a slash

            display.setRotation(player.getEyeLocation().getYaw()+rotation, player.getEyeLocation().getPitch());

            // Remove the entity after 1 tick to mimic a particle
            new BukkitRunnable() {
                @Override
                public void run() {
                    display.remove();
                }
            }.runTaskLater(plugin, 60);

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
