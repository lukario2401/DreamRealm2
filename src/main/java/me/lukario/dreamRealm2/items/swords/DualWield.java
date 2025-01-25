package me.lukario.dreamRealm2.items.swords;


import me.lukario.dreamRealm2.RayCast;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.lukario.dreamRealm2.RayCast.performRaycast;


public class DualWield implements Listener {

 private final Map<Player, Long> lastParryTime = new HashMap<>(); // Track when player last parried
    private final Map<Player, Boolean> isParrying = new HashMap<>(); // Track if the player is currently parrying


    private final Plugin plugin;

    public DualWield(Plugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createDualWield() {
        ItemStack shadowDance = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = shadowDance.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_PURPLE + "Death");
            meta.setLore(Arrays.asList(ChatColor.GOLD + "Wielded by death its self"));
            meta.setCustomModelData(4);
            shadowDance.setItemMeta(meta);
        }
        return shadowDance;
    }

    public void setPlayerMetadata(Player player, String tag) {
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, tag), PersistentDataType.BYTE, (byte) 1);
    }

    public boolean hasPlayerMetadata(Player player, String tag) {
        return player.getPersistentDataContainer().has(new NamespacedKey(plugin, tag), PersistentDataType.BYTE);
    }

    public void removePlayerMetadata(Player player, String tag) {
        player.getPersistentDataContainer().remove(new NamespacedKey(plugin, tag));
    }

    @EventHandler
    public void onEntityDamageByEntityy(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(checkIfPlayerIsHoldingCorrectItem(player)){


                List<LivingEntity> nearbyEntities = getNearbyLivingEntitiesAroundPlayer(event.getEntity(), 6);

                for (LivingEntity entity : nearbyEntities) {
                    if(entity != event.getDamager()){
                        entity.damage(21.0);


                        entity.getWorld().spawnParticle(Particle.DUST,entity.getLocation().add(0, 1, 0),10,0.2, 0.5, 0.2,
                        new Particle.DustOptions(Color.WHITE, 1.0f));
                        entity.getWorld().spawnParticle(Particle.DUST,entity.getLocation().add(0, 1, 0),10,0.2, 0.5, 0.2,
                        new Particle.DustOptions(Color.BLACK, 1.0f));
                        entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK,entity.getLocation().add(0, 1, 0),10,0.2, 0.5, 0.2);

                    }
                }

            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

       if (checkIfPlayerIsHoldingCorrectItem(player)){

        boolean isSneaking = player.isSneaking();
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                setPlayerMetadata(player, "fl");
                setPlayerMetadata(player, isSneaking ? "sl" : "sr");
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                setPlayerMetadata(player, "fr");
                setPlayerMetadata(player, isSneaking ? "sr" : "sl");
                break;
        }

        if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sr")) {

                List<LivingEntity> nearbyEntities = getNearbyLivingEntitiesAroundPlayer(player, 3);

                for (LivingEntity entity : nearbyEntities) {
                    if(entity != player){
                        entity.damage(21.0);


                        entity.getWorld().spawnParticle(Particle.DUST,entity.getLocation().add(0, 1, 0),10,0.2, 0.5, 0.2,
                        new Particle.DustOptions(Color.WHITE, 1.0f));
                        entity.getWorld().spawnParticle(Particle.DUST,entity.getLocation().add(0, 1, 0),10,0.2, 0.5, 0.2,
                        new Particle.DustOptions(Color.BLACK, 1.0f));
                        entity.getWorld().spawnParticle(Particle.SWEEP_ATTACK,entity.getLocation().add(0, 1, 0),10,0.2, 0.5, 0.2);

                    }
                }


//          player.sendMessage(ChatColor.RED + " Left click");
            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sr");

        } else if (hasPlayerMetadata(player, "fl") && hasPlayerMetadata(player, "sl")) {


            player.getWorld().playSound(player,Sound.ENTITY_ENDER_DRAGON_GROWL,1,1);
            RayCast rayCast = new RayCast(plugin);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,0,1,12,Particle.SOUL_FIRE_FLAME,0,0,1,0.2);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,0,0.2,12,Particle.SOUL_FIRE_FLAME,0,0.05F,10,0.2);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,1,0.2,11,Particle.SOUL_FIRE_FLAME,0,0.1F,10,0.4);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,1,0.2,11,Particle.SOUL_FIRE_FLAME,0,0.2F,10,0.4);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,2,0.2,10,Particle.SOUL_FIRE_FLAME,0,0.3F,10,0.6);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,3,0.2,9,Particle.SOUL_FIRE_FLAME,0,0.4F,10,0.8);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,4,0.2,8,Particle.SOUL_FIRE_FLAME,0,0.5F,10,1.0);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,5,0.2,7,Particle.SOUL_FIRE_FLAME,0,0.6F,10,1.2);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,6,0.2,6,Particle.SOUL_FIRE_FLAME,0,0.6F,10,1.4);
            rayCast.rayCastWithIntervalsAndParticleSpread(plugin,player,7,0.2,5,Particle.SOUL_FIRE_FLAME,0,0.6F,10,1.6);

//          player.sendMessage(ChatColor.RED + "Sneak Left click");
            removePlayerMetadata(player, "fl");
            removePlayerMetadata(player, "sl");

        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sl")) {
            if (event.getHand() != EquipmentSlot.OFF_HAND) {
                triggerParry(player);
    //          player.sendMessage(ChatColor.RED + "Right click");
                removePlayerMetadata(player, "fr");
                removePlayerMetadata(player, "sl");
            }
        } else if (hasPlayerMetadata(player, "fr") && hasPlayerMetadata(player, "sr")) {

            player.teleport(Objects.requireNonNull(performRaycast(player, 24D, 0.5D)));

//            player.sendMessage(ChatColor.RED + "Sneak Right click");
            removePlayerMetadata(player, "fr");
            removePlayerMetadata(player, "sr");
        }
        }
    }

    private static boolean checkIfPlayerIsHoldingCorrectItem(Player player) {
    // Get the item in the player's main hand
    ItemStack item = player.getInventory().getItemInMainHand();
    if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
        return false;
    }

    ItemStack leftItem = player.getInventory().getItemInOffHand();
    if (leftItem == null || leftItem.getType() == Material.AIR || !leftItem.hasItemMeta()) {
        return false;
    }

    // Validate main hand item
    ItemMeta meta = item.getItemMeta();
    if (!meta.getDisplayName().equals(ChatColor.DARK_PURPLE + "Death") ||
        meta.getLore() == null ||
        !meta.getLore().contains(ChatColor.GOLD + "Wielded by death its self")) {
        return false;
    }

    // Validate off-hand item
    ItemMeta leftMeta = leftItem.getItemMeta();
    if (!leftMeta.getDisplayName().equals(ChatColor.DARK_PURPLE + "Death") ||
        leftMeta.getLore() == null ||
        !leftMeta.getLore().contains(ChatColor.GOLD + "Wielded by death its self")) {
        return false;
    }

    return true;
}

private List<LivingEntity> getNearbyLivingEntitiesAroundPlayer(Entity player, double radius) {
    List<LivingEntity> livingEntities = new ArrayList<>();
    World world = player.getWorld();

    for (Entity entity : world.getNearbyEntities(player.getLocation(), radius, radius, radius)) {
        if (entity instanceof LivingEntity livingEntity && !(livingEntity instanceof ArmorStand)) {
            livingEntities.add(livingEntity);
        }
    }

    return livingEntities;
}

private void triggerParry(Player player) {
    long currentTime = System.currentTimeMillis();
    long cooldownTime = 4000; // Default cooldown in milliseconds (4 seconds)

    // Check if parry is on cooldown
    if (lastParryTime.containsKey(player) && currentTime - lastParryTime.get(player) < cooldownTime) {
        player.sendMessage(ChatColor.RED + "Parry is on cooldown!");
        return;
    }

    // Set parry state
    lastParryTime.put(player, currentTime);
    isParrying.put(player, true);
    player.sendMessage(ChatColor.GREEN + "You are immune and can parry incoming attacks!");

    // End parry effect after 10 ticks
    new BukkitRunnable() {
        @Override
        public void run() {
            if (isParrying.getOrDefault(player, false)) {
                isParrying.put(player, false);
                player.sendMessage(ChatColor.RED + "Parry effect ended.");
            }
        }
    }.runTaskLater(plugin, 20); // Parry lasts 10 ticks (0.5 seconds)
}

@EventHandler
public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player damagedPlayer)) return;

    // Check if the damaged player is parrying
    if (isParrying.getOrDefault(damagedPlayer, false)) {
        Entity attacker = event.getDamager();

        // Reflect damage if the attacker is a LivingEntity
        if (attacker instanceof LivingEntity livingAttacker) {
            double reflectedDamage = (event.getFinalDamage() + 6) * 4;
            livingAttacker.damage(reflectedDamage);
            damagedPlayer.sendMessage(ChatColor.YELLOW + "Damage reflected back to " + attacker.getName() + "!");
        }

        // Reset parry cooldown to 0
        lastParryTime.put(damagedPlayer, 4000L);
        event.setCancelled(true); // Prevent damage to the player
    }
}

}
