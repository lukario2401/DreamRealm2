package me.lukario.dreamRealm2.items.special.builder;

import com.mojang.brigadier.Message;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Fence implements Listener {

    private final Plugin plugin;

    public Fence(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Fence";
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

    @EventHandler
    public void fenceUsed(PlayerInteractEvent event){

        Player player = event.getPlayer();

        if (!isHoldingTheCorrectItem(player)){return;}

        if (Action.RIGHT_CLICK_BLOCK==event.getAction()||Action.RIGHT_CLICK_AIR==event.getAction()){
            ArmorStand armorStand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);

            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setSmall(true);

            armorStand.setItem(EquipmentSlot.HEAD, new ItemStack(Material.HEAVY_CORE, 1));

        }
        if (Action.LEFT_CLICK_BLOCK==event.getAction()||Action.LEFT_CLICK_AIR==event.getAction()){
            int runningFor = 1;
            ArmorStand firstArmorStand = null;
            ArmorStand secondArmorStand = null;
            for (LivingEntity livingEntity : player.getLocation().getNearbyLivingEntities(12)){
                if (livingEntity instanceof ArmorStand){
                    ItemStack helmet = livingEntity.getEquipment().getHelmet();
                    if (helmet != null && helmet.getType() == Material.HEAVY_CORE){

                        if (runningFor==1){
                            firstArmorStand = (ArmorStand) livingEntity;
                        } else if (runningFor==2) {
                            secondArmorStand = (ArmorStand) livingEntity;
                        } else{
                            break;
                        }
                        runningFor+=1;

                    }
                }
            }
            if (firstArmorStand!=null && secondArmorStand!=null){
                beamForArmorStands(firstArmorStand,secondArmorStand);
            }
        }
    }

    private void beamForArmorStands(ArmorStand firstArmorStand, ArmorStand secondArmorStand) {
        Location startLocation = firstArmorStand.getLocation().add(0, 0.8, 0);
        Location endLocation = secondArmorStand.getLocation().add(0, 0.8, 0);
        double distance = startLocation.distance(endLocation);
        Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();

        new BukkitRunnable() {
            int timeAlive = 0;
            final Set<UUID> hitEntities = new HashSet<>();

            @Override
            public void run() {
                if (timeAlive >= 400 || !firstArmorStand.isValid() || !secondArmorStand.isValid()) {
                    firstArmorStand.remove();
                    secondArmorStand.remove();
                    this.cancel();
                    return;
                }

                // Reset hit entities each tick
                hitEntities.clear();

                Location current = startLocation.clone();
                for (double i = 0; i < distance; i += 0.25) {
                    Vector step = direction.clone().multiply(i);
                    current = startLocation.clone().add(step);

                    // Spawn particles
                    current.getWorld().spawnParticle(Particle.ENCHANTED_HIT, current, 1, 0, 0, 0, 0);

                    // Handle entity damage and effects
                    for (LivingEntity entity : current.getNearbyLivingEntities(0.25)) {
                        if (!(entity instanceof ArmorStand) && !hitEntities.contains(entity.getUniqueId())) {
                            hitEntities.add(entity.getUniqueId());
                            entity.damage(6);
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 5, true));
                        }
                    }
                }

                timeAlive++;
            }
        }.runTaskTimer(plugin, 0, 1);


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
