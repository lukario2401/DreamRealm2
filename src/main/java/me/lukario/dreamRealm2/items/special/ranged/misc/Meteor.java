package me.lukario.dreamRealm2.items.special.ranged.misc;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
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

public class Meteor implements Listener {

    private final Plugin plugin;

    public Meteor(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Meteor";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Cosmos";
    private static final Material ITEM_MATERIAL = Material.DIAMOND_SHOVEL;

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
    public void meteorUsed(PlayerInteractEvent event) {
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

                LivingEntity livingEntity = meteorLocationDrop(player);

                if (livingEntity!=null){
                    meteorDrop(player,livingEntity);
                }

                cooldown.put(uuid, 10f);

            } else {
                player.sendMessage(org.bukkit.ChatColor.DARK_RED + "Your Cooldown is: " + (cooldown.get(uuid) / 20) + " Seconds");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        }
    }

    private LivingEntity meteorLocationDrop(Player player) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i = 0; i <= 32; i += 0.5f) {

            Location current = location.clone().add(direction.clone().multiply(i));

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)) {

                if (!livingEntity.equals(player)) {
                    return livingEntity;
                }
            }
        }
        return null;
    }

    private void meteorDrop(Player player, LivingEntity livingEntity){

        Location current = livingEntity.getLocation();

        FallingBlock meteor = current.add(0,10,0).getBlock().getWorld().spawnFallingBlock(current, Material.MAGMA_BLOCK.createBlockData());

        meteor.setDropItem(false);
        meteor.setCancelDrop(true);
        meteor.setInvulnerable(true);

        new BukkitRunnable(){
            int timeAlive=0;
            @Override
            public void run(){
                if (timeAlive>=200){
                    meteor.remove();
                    this.cancel();
                }

                Location meteorLocation = meteor.getLocation();

                if (meteorLocation.add(0,-0.1,0).getBlock().getType()!=Material.AIR){
                    for (LivingEntity livingEntity1 : meteorLocation.getNearbyLivingEntities(6)){
                        Misc.damageNoTicks(livingEntity1,21,player);
                        timeAlive+=200;
                    }
                    for (LivingEntity livingEntity1 : meteorLocation.getNearbyLivingEntities(4)){
                        Misc.damageNoTicks(livingEntity1,42,player);
                        timeAlive+=200;
                    }
                    for (LivingEntity livingEntity1 : meteorLocation.getNearbyLivingEntities(2)){
                        Misc.damageNoTicks(livingEntity1,52,player);
                        timeAlive+=200;
                    }
                }
                timeAlive+=1;
            }
        }.runTaskTimer(plugin,0,1);
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
