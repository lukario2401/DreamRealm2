package me.lukario.dreamRealm2.items.special.ranged.misc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
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

public class YetiSword implements Listener {

    private final Plugin plugin;

    public YetiSword(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Yeti Sword";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Yeti";
    private static final Material ITEM_MATERIAL = Material.IRON_SHOVEL;

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
    public void yetiSwordUsed(PlayerInteractEvent event) {
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

                yetiSwordChunk(player);

                cooldown.put(uuid, 10f);

            } else {
                player.sendMessage(org.bukkit.ChatColor.DARK_RED + "Your Cooldown is: " + (cooldown.get(uuid) / 20) + " Seconds");
                player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        }
    }

    private void yetiSwordChunk(Player player) {

        Location location = player.getEyeLocation().add(0, 1, 0);
        Vector direction = location.getDirection();

        FallingBlock ice = location.getWorld().spawnFallingBlock(location, Material.ICE.createBlockData());

        ice.setCancelDrop(true);
        ice.setGravity(false);
        ice.setDropItem(false);

        Vector newDirection = direction.clone().add(new Vector(0, 1, 0));
        ice.setVelocity(newDirection.multiply(0.5));

        new BukkitRunnable() {
            int timeAlive = 0;
            boolean hasExploded = false;

            @Override
            public void run() {
                if (!ice.isValid()) {
                    timeAlive += 100;
                }
                if (timeAlive >= 25) {
                    this.cancel();
                    return;
                }

                Vector slowDown = newDirection.clone();
                if (timeAlive > 10) {
                    slowDown = newDirection.clone().add(new Vector(0, -1, 0));
                    ice.setVelocity(slowDown);
                }
                if (timeAlive > 15) {
                    slowDown = newDirection.clone().add(new Vector(0, -1.5, 0));
                    ice.setVelocity(slowDown);
                }
                if (timeAlive > 20) {
                    slowDown = newDirection.clone().add(new Vector(0, -2, 0));
                    ice.setVelocity(slowDown);
                }

                // Check for explosion only if it hasn't happened yet
                if (!hasExploded) {
                    Location checkLoc = ice.getLocation().add(0, -1, 0);
                    if (checkLoc.getBlock().getType() != Material.AIR||checkLoc.add(0.5,0,0).getBlock().getType() != Material.AIR||checkLoc.add(-0.5,0,0).getBlock().getType() != Material.AIR||checkLoc.add(0.5,0,0.5).getBlock().getType() != Material.AIR||checkLoc.add(0.5,0,0.5).getBlock().getType() != Material.AIR) {

                        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,1,255,false));

                        checkLoc.getWorld().createExplosion(checkLoc.add(0,1,0), 16.5f, false,false);
                        checkLoc.getWorld().createExplosion(checkLoc.add(0,1.1,0), 16.5f, false,false);
                        checkLoc.getWorld().createExplosion(checkLoc.add(0,1.2,0), 15.5f, false,false);


                        hasExploded = true;
                        ice.remove();
                        this.cancel();
                        return;
                    }
                }

                timeAlive += 1;
            }
        }.runTaskTimer(plugin, 0, 1);
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