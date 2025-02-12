package me.lukario.dreamRealm2.items.special.builder;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;

public class Portal implements Listener {

    private final Plugin plugin;

    public Portal(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Portal";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating ####";
    private static final Material ITEM_MATERIAL = Material.AMETHYST_SHARD;

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

    private ArmorStand firstArmorStand = null;
    private ArmorStand secondArmorStand = null;

    @EventHandler
    public void portalUse(PlayerInteractEvent event){


        Player player = event.getPlayer();

        if (!isHoldingTheCorrectItem(player)){return;}


        if (Action.LEFT_CLICK_AIR==event.getAction()||Action.LEFT_CLICK_BLOCK==event.getAction()){

            if (firstArmorStand!=null){            firstArmorStand.remove();            }
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);

            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setNoPhysics(true);
            armorStand.setSmall(true);

            armorStand.setItem(EquipmentSlot.HEAD,new ItemStack(Material.ENDER_EYE));

            firstArmorStand = armorStand;

            if (firstArmorStand !=null && secondArmorStand!=null){
                player.sendMessage("g");
                beamEffectForPortal(firstArmorStand,secondArmorStand);
            }


        }

        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){

            if (secondArmorStand!=null){secondArmorStand.remove();            }
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);

            armorStand.setMarker(true);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setNoPhysics(true);
            armorStand.setSmall(true);

            armorStand.setItem(EquipmentSlot.HEAD,new ItemStack(Material.ENDER_EYE));

            secondArmorStand = armorStand;

            if (firstArmorStand !=null && secondArmorStand!=null){
                player.sendMessage("g");
                beamEffectForPortal(firstArmorStand,secondArmorStand);
            }


        }

    }

private void beamEffectForPortal(ArmorStand firstArmorStand, ArmorStand secondArmorStand) {
    // Track cooldowns per entity
    HashMap<LivingEntity, Integer> teleportCooldowns = new HashMap<>();

    new BukkitRunnable() {
        int aliveTime = 0;
        final Location startLocation = firstArmorStand.getLocation().add(0, 1, 0);
        final Location endLocation = secondArmorStand.getLocation().add(0, 1, 0);
        final double distance = startLocation.distance(endLocation);
        final Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();

        @Override
        public void run() {
            // Cleanup if armor stands are removed
            if (!firstArmorStand.isValid() || !secondArmorStand.isValid()) {
                cancel();
                return;
            }

            // Particle beam (unchanged)
            if (aliveTime % 2 == 0) { // Reduce particle frequency for performance
                Location current = startLocation.clone();
                for (double i = 0; i < distance; i += 0.25) {
                    current.getWorld().spawnParticle(Particle.END_ROD, current, 1, 0, 0, 0, 0);
                    current.add(direction.clone().multiply(0.25));
                }
            }

            // Check for entities at both portals
            checkAndTeleport(firstArmorStand, secondArmorStand, teleportCooldowns);
            checkAndTeleport(secondArmorStand, firstArmorStand, teleportCooldowns);

            // Decrement cooldowns
            teleportCooldowns.entrySet().removeIf(entry -> {
                if (entry.getValue() <= 0) return true;
                entry.setValue(entry.getValue() - 1);
                return false;
            });

            aliveTime++;
            if (aliveTime >= 1200) cancel(); // Portal expires after 60 seconds
        }
    }.runTaskTimer(plugin, 0, 1);
}

private void checkAndTeleport(ArmorStand source, ArmorStand destination, HashMap<LivingEntity, Integer> cooldowns) {
    for (LivingEntity entity : source.getLocation().getNearbyLivingEntities(1.5)) { // Increased radius to 1.5 blocks
        if (entity instanceof ArmorStand || cooldowns.containsKey(entity)) continue;

        // Preserve entity's original direction
        Location destLoc = destination.getLocation().add(0, 1, 0);
        destLoc.setDirection(entity.getLocation().getDirection());

        entity.teleport(destLoc);
        cooldowns.put(entity, 40); // 2-second cooldown per entity
    }
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
