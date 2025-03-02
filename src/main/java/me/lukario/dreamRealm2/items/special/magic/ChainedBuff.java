package me.lukario.dreamRealm2.items.special.magic;

import me.lukario.dreamRealm2.Misc;
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
import org.bukkit.event.player.PlayerItemHeldEvent;
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
        new BukkitRunnable(){
            @Override public void run(){
                Set<UUID> uuid = cooldownLeft.keySet();
                for (UUID uuid1 : uuid){
                    if (cooldownLeft.get(uuid1)>0f){
                        cooldownLeft.put(uuid1,cooldownLeft.get(uuid1)-1f);
                    }
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

    private static final HashMap<UUID,Float> cooldown = new HashMap<>();
    private static final HashMap<UUID,Float> cooldownLeft = new HashMap<>();
    private static final HashMap<UUID,Boolean> hasUsedItem = new HashMap<>();


    public void playerHeldItemEventForParticle(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (hasUsedItem.get(uuid)==null){
            hasUsedItem.put(uuid,false);
        }
        if (hasUsedItem.get(uuid)==false){
            particleForTheClaw(player);
            hasUsedItem.put(uuid,true);
        }
    }

    @EventHandler
    public void chainedBuffUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
//        UUID uuid = player.getUniqueId();
//        if (hasUsedItem.get(uuid)==null){
//            hasUsedItem.put(uuid,false);
//        }
//        if (hasUsedItem.get(uuid)==false){
//            particleForTheClaw(player);
//            hasUsedItem.put(uuid,true);
//        }
        if (cooldown.get(player.getUniqueId())==null){
            cooldown.put(player.getUniqueId(),0f);
        }
        if (cooldownLeft.get(player.getUniqueId())==null){
            cooldownLeft.put(player.getUniqueId(),0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;}

        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){
            if (cooldown.get(player.getUniqueId())==0){
    //            createSlash(player,45f);
    //            createSlash(player,-45f);

                LivingEntity livingEntity = getLivingEntity(player);
                if (livingEntity!=null){
                    cooldown.put(player.getUniqueId(),10f);
                    attt(player,livingEntity);
                }
            }else{
                player.sendMessage(ChatColor.DARK_RED + "Your cooldown is: " + Math.round(cooldown.get(player.getUniqueId())/20));
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
        if (Action.LEFT_CLICK_AIR==event.getAction()||Action.LEFT_CLICK_BLOCK==event.getAction()){
            if (cooldownLeft.get(player.getUniqueId())==0){
    //            createSlash(player,45f);
    //            createSlash(player,-45f);

                LivingEntity livingEntity = getLivingEntity(player);
                if (livingEntity!=null){
                    cooldownLeft.put(player.getUniqueId(),10f);
                    att(player,livingEntity);
                }
            }else{
                player.sendMessage(ChatColor.DARK_RED + "Your cooldown is: " + Math.round(cooldownLeft.get(player.getUniqueId())/20));
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }

    }

    private void particleForTheClaw(Player player){
        new BukkitRunnable(){
            @Override
            public void run(){
                if (isHoldingTheCorrectItem(player)){
                    Location location = player.getEyeLocation().add(0,-0.5,0);
                    Vector direction = location.getDirection();

                    for (float i =0; i<=1.5;i+=0.1f){
                    Location current = location.clone().add(direction.clone().multiply(i));

                    Vector rightOffset = direction.clone().crossProduct(new Vector(0, -0.5, 0)).normalize().multiply(-i);

                    current.getWorld().spawnParticle(Particle.FLAME,current.clone().add(rightOffset),1,0,0,0,0);
                    }
                    for (float j=1.5f; j<=3;j+=0.1f){
                    Location current = location.clone().add(direction.clone().multiply(j));

                    Vector rightOffset = direction.clone().crossProduct(new Vector(0, -0.5, 0)).normalize().multiply(j-3);

                    current.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,current.clone().add(rightOffset),1,0,0,0,0);
                    }
                }else{
                    hasUsedItem.put(player.getUniqueId(),false);
                }
            }
        }.runTaskTimer(plugin,0,1);
    }

    private LivingEntity getLivingEntity(Player player){

        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i =0; i<=96; i+=0.5f){
            if (i<32){
                Location current = location.clone().add(direction.clone().multiply(i));
                for (LivingEntity livingEntity:current.getNearbyLivingEntities(1)){
                    if (!livingEntity.equals(player)){
                        if (!livingEntity.isDead()){
                            return livingEntity;
                        }
                    }
                }
            }
            if (i>32){
                Location current = location.clone().add(direction.clone().multiply(i));
                for (LivingEntity livingEntity:current.getNearbyLivingEntities(2)){
                    if (!livingEntity.equals(player)){
                        if (!livingEntity.isDead()){
                            return livingEntity;
                        }
                    }
                }
            }
            if (i>64){
                Location current = location.clone().add(direction.clone().multiply(i));
                for (LivingEntity livingEntity:current.getNearbyLivingEntities(3)){
                    if (!livingEntity.equals(player)){
                        if (!livingEntity.isDead()){
                            return livingEntity;
                        }
                    }
                }
            }
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

    private void att(Player player, LivingEntity livingEntity) {
    // Start and end points (a bit above each entity)
    Location startLocation = player.getLocation().add(0, 1, 0);
    Location endLocation = livingEntity.getLocation().add(0, 1, 0);

    // Midpoint between start and end
    Location controlLocation = startLocation.clone().add(
            endLocation.clone().subtract(startLocation).multiply(0.5)
    );

    // Get the player's facing direction (horizontal)
    Vector direction = player.getLocation().getDirection();
    // Flatten out the Y, so we only rotate in the horizontal plane
    direction.setY(0);
    direction.normalize();

    // Rotate 90° to get a 'right' vector
    // If direction = (x, 0, z), a perpendicular is (z, 0, -x).
    Vector right = new Vector(direction.getZ(), 0, -direction.getX()).normalize();

    // Adjust how far "right" you want the curve to arc
    right.multiply(6); // Try bigger or smaller values for a different arc

    // Add the rightward offset to the control location
    controlLocation.add(right);

    // Number of interpolation steps (more steps = smoother curve)
    int steps =  20;

    for (int i = 0; i <= steps; i++) {
        double t = (double) i / steps;
        double u = 1 - t;

        // Quadratic Bézier interpolation:
        double x = (u * u * startLocation.getX())
                 + (2 * u * t * controlLocation.getX())
                 + (t * t * endLocation.getX());

        double y = (u * u * startLocation.getY())
                 + (2 * u * t * controlLocation.getY())
                 + (t * t * endLocation.getY());

        double z = (u * u * startLocation.getZ())
                 + (2 * u * t * controlLocation.getZ())
                 + (t * t * endLocation.getZ());

        // Current position on the curve
        Location current = new Location(startLocation.getWorld(), x, y, z);

        // Spawn a flame particle
        current.getWorld().spawnParticle(Particle.FLAME, current, 1,0,0,0,0);

        // Damage nearby living entities (excluding the caster)
        for (LivingEntity near : current.getNearbyLivingEntities(2)) {
            if (!near.equals(player)) {

                livingEntity.getWorld().spawnParticle(Particle.EXPLOSION,current,1,0,0,0,0);
                livingEntity.getWorld().playSound(current,Sound.ENTITY_GENERIC_EXPLODE,3,1);
                Misc.damageNoTicks(livingEntity,24,player);
            }
        }
    }
}

private void attt(Player player, LivingEntity livingEntity) {
    // Start and end points (slightly above each entity)
    Location startLocation = player.getLocation().add(0, 1, 0);
    Location endLocation = livingEntity.getLocation().add(0, 1, 0);

    // Midpoint between start and end
    Location controlLocation = startLocation.clone().add(
            endLocation.clone().subtract(startLocation).multiply(0.5)
    );

    // Get the player's facing direction, flattened horizontally
    Vector direction = player.getLocation().getDirection();
    direction.setY(0);
    direction.normalize();

    // Create a "left" vector by rotating the direction vector 90° left
    // For direction (x, 0, z), left is (-z, 0, x)
    Vector left = new Vector(-direction.getZ(), 0, direction.getX()).normalize();

    // Increase the curve by applying a larger leftward offset
    left.multiply(8);  // Increased from 2 to 4 for a more pronounced curve

    // Offset the control point to the left
    controlLocation.add(left);

    // Number of interpolation steps (more steps gives a smoother curve)
    int steps = 20;

    for (int i = 0; i <= steps; i++) {
        double t = (double) i / steps;
        double u = 1 - t;

        // Quadratic Bézier interpolation:
        double x = (u * u * startLocation.getX())
                 + (2 * u * t * controlLocation.getX())
                 + (t * t * endLocation.getX());

        double y = (u * u * startLocation.getY())
                 + (2 * u * t * controlLocation.getY())
                 + (t * t * endLocation.getY());

        double z = (u * u * startLocation.getZ())
                 + (2 * u * t * controlLocation.getZ())
                 + (t * t * endLocation.getZ());

        // Current position on the curve
        Location current = new Location(startLocation.getWorld(), x, y, z);

        // Spawn a flame particle
        current.getWorld().spawnParticle(Particle.FLAME, current, 1,0,0,0,0);

        // Damage nearby living entities (excluding the caster)
        for (LivingEntity near : current.getNearbyLivingEntities(2)) {
            if (!near.equals(player)) {

                livingEntity.getWorld().spawnParticle(Particle.EXPLOSION,current,1,0,0,0,0);
                livingEntity.getWorld().playSound(current,Sound.ENTITY_GENERIC_EXPLODE,3,1);
                Misc.damageNoTicks(livingEntity,24,player);
            }
        }
    }
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
