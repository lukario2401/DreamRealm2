package me.lukario.dreamRealm2.items.ranged.bow;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class JujuShortBow implements Listener {

    private final Plugin plugin;

    // Constants for configuration
    private static final String BOW_NAME = ChatColor.LIGHT_PURPLE + "Juju Shortbow";
    private static final String BOW_LORE = ChatColor.YELLOW + "Powerful bow made to destroy any item";
    private static final double EXTRA_DAMAGE = 26.0;
    private static final String ARROW_TAG = "juju_arrow";

    public JujuShortBow(Plugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createJujuShortBow() {
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(BOW_NAME);
            meta.setLore(Arrays.asList(BOW_LORE));
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (isHoldingJujuShortBow(player)) {
            boolean isSneaking = player.isSneaking();
            switch (event.getAction()) {
                case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> shootArrows(player, isSneaking);
            }
        }
    }

    private void shootArrows(Player player, boolean isSneaking) {
        float[] offsets = isSneaking ? new float[]{-0.1F, 0.0F, 0.1F} : new float[]{-0.05F, 0.0F, 0.05F};
        for (float offset : offsets) {
            shootArrow(player, offset, 16F);
        }
        player.playSound(player, Sound.ENTITY_ARROW_SHOOT, 1, 1);
    }

    private void shootArrow(Player player, float offset, float speed) {
        World world = player.getWorld();
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize().add(applyOffset(player, offset));

        Arrow arrow = world.spawn(eyeLocation.add(direction.multiply(0.5)), Arrow.class);
        arrow.setShooter(player);
        arrow.setVelocity(direction.multiply(speed));
        arrow.setCritical(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        arrow.getPersistentDataContainer().set(new NamespacedKey(plugin, ARROW_TAG), PersistentDataType.BYTE, (byte) 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || !arrow.isValid()) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1);
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;

        if (arrow.getPersistentDataContainer().has(new NamespacedKey(plugin, ARROW_TAG), PersistentDataType.BYTE)) {
            if (event.getEntity() instanceof LivingEntity target) {
                target.damage(EXTRA_DAMAGE);
                 target.setNoDamageTicks(0);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
//                target.getWorld().spawnParticle(Particle.CRIT, target.getLocation(), 10, 0.2, 0.2, 0.2, 0.05);
                arrow.remove();
            }
        }
    }

    private boolean isHoldingJujuShortBow(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.IRON_HOE) return false;

        ItemMeta meta = item.getItemMeta();
        return meta != null && BOW_NAME.equals(meta.getDisplayName()) &&
               meta.getLore() != null && meta.getLore().contains(BOW_LORE);
    }

    private Vector applyOffset(Player player, float offset) {
        Vector direction = player.getEyeLocation().getDirection();
        Vector side = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
        return side.multiply(offset);
    }
}
