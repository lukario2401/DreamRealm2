package me.lukario.dreamRealm2.items.special.ranged.misc;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class Link implements Listener {

    private final Plugin plugin;

    public Link(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Link";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Space";
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

                LivingEntity livingEntity = getLivingEntity(player,32);

                if (livingEntity!=null){

                    runTheTwoPlaceRayCasts(player,livingEntity);
                    runTheTwoPlaceRayCasts(player,livingEntity);
                    runTheTwoPlaceRayCasts(player,livingEntity);
                    runTheTwoPlaceRayCasts(player,livingEntity);
                    runTheTwoPlaceRayCasts(player,livingEntity);

                }
                cooldown.put(uuid, 10f);
            } else {
                player.sendMessage(org.bukkit.ChatColor.DARK_RED + "Your Cooldown is: " + (cooldown.get(uuid) / 20) + " Seconds");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        }
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (cooldown.get(uuid) == 0) {
                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 1, 1);

                LivingEntity livingEntity = getLivingEntity(player,32);

                if (livingEntity!=null){

                    runTheTwoPlaceRayCasts(livingEntity);
                    runTheTwoPlaceRayCasts(livingEntity);
                    runTheTwoPlaceRayCasts(livingEntity);
                    runTheTwoPlaceRayCasts(livingEntity);
                    runTheTwoPlaceRayCasts(livingEntity);

                }
                cooldown.put(uuid, 10f);
            } else {
                player.sendMessage(org.bukkit.ChatColor.DARK_RED + "Your Cooldown is: " + (cooldown.get(uuid) / 20) + " Seconds");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        }
    }

    private void runTheTwoPlaceRayCasts(Player player,LivingEntity livingEntity){

        for (float i =0; i <= 5; i+=1){

            int x = (int) (Math.random() * 11) - 5;
            int y = (int) (Math.random() * 6);
            int z = (int) (Math.random() * 11) - 5;

            Location locationOfPlayerWithRandom = player.getLocation().add(x,y,z);
            twoPlaceRayCast(locationOfPlayerWithRandom,livingEntity);
        }
    }

    private void runTheTwoPlaceRayCasts(LivingEntity livingEntity){
        for (float i =0; i <= 5; i+=1){

            int x = (int) (Math.random() * 11) - 5;
            int y = (int) (Math.random() * 6);
            int z = (int) (Math.random() * 11) - 5;

            Location locationOfPlayerWithRandom = livingEntity.getLocation().add(x,y,z);
            twoPlaceRayCast(locationOfPlayerWithRandom,livingEntity);
        }
    }

    private void twoPlaceRayCast(Location startLocationn, LivingEntity livingEntity){

        Location startLocation = startLocationn.add(0,1,0);
        Location endLocation = livingEntity.getLocation().add(0,1,0);

        Vector direction = endLocation.toVector().subtract(startLocation.toVector()).normalize();
        double distance = startLocation.distance(endLocation);

        for (float i = 0; i <= distance; i+=0.5f){
            Location currentLocation = startLocation.clone().add(direction.clone().multiply(i));

            currentLocation.getWorld().spawnParticle(Particle.ENCHANT,currentLocation,10,0.1,0.1,0.1,0);
        }

    }

    private LivingEntity getLivingEntity(Player player,float range){
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection().normalize();

        for (float i =0; i <= range; i+=0.5f){
            Location current = location.clone().add(direction.clone().multiply(i));

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)){
                if (!livingEntity.equals(player)){
                    return livingEntity;
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
