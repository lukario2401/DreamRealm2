package me.lukario.dreamRealm2.items.swords.cultivation;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class JadeSword implements Listener {

    private final Plugin plugin;

    private static final String ITEM_NAME  = ChatColor.of("#D88F07") + "Jade Blade";
    private static final String ITEM_LORE  = ChatColor.YELLOW + "Summons six spinning swords";
    private static final Material ITEM_MATERIAL = Material.IRON_SWORD;

    private static final HashMap<UUID,Float> amountOfArmorStands = new HashMap<>();
    private static final HashMap<UUID,Float> newSwordCooldown = new HashMap<>();

    public JadeSword(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
        runEveryTickForPlayers();
    }

    private void cooldownManagement() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<UUID> keyLeft = newSwordCooldown.keySet();
                for (UUID uuid : keyLeft) {
                    if (newSwordCooldown.get(uuid) > 0) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player!=null){
                            if (isHoldingTheCorrectItem(player)){
                                if (amountOfArmorStands.get(uuid)<6){
                                    newSwordCooldown.put(uuid, newSwordCooldown.get(uuid) - 1);
                                }
                            }
                        }
                    }else{
                        Player player = Bukkit.getPlayer(uuid);
                        if (player!=null){
                            if (amountOfArmorStands.get(uuid)<6){
                                newSwordCooldown.put(uuid,60f);
                                amountOfArmorStands.put(uuid,amountOfArmorStands.get(uuid)+1);
                                player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin,0,1);
    }


    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(2);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (newSwordCooldown.get(uuid)==null){
            newSwordCooldown.put(uuid,0f);
        }
        if (amountOfArmorStands.get(uuid)==null){
            amountOfArmorStands.put(uuid,0f);
        }
    }

    @EventHandler
    public void clawsUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!isHoldingTheCorrectItem(player)){return;}

        if (event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK){
            leftClickJadeSword(player,-20f);
            leftClickJadeSword(player,0f);
            leftClickJadeSword(player,20f);
        }
        if (event.getAction()== Action.RIGHT_CLICK_AIR||event.getAction()==Action.RIGHT_CLICK_BLOCK) {
           player.sendMessage("Right");
           if (amountOfArmorStands.get(uuid)>0){
                amountOfArmorStands.put(uuid,amountOfArmorStands.get(uuid)-1);
           }
        }
    }

    private void leftClickJadeSword(Player player, Float degree){
        Location location = player.getEyeLocation();
        location.setYaw(location.getYaw()+degree);
        Vector direction = location.getDirection().normalize();
        UUID uuid = player.getUniqueId();

        for (float i = 0; i<=4f; i+=0.5f){

            Location current = location.clone().add(direction.clone().multiply(i));
            current.getWorld().spawnParticle(Particle.SOUL,current,1,0,0,0,0);

            if (current.getBlock().getType()!=Material.AIR){
                i+=256;
            }

            for (LivingEntity livingEntity : current.getNearbyLivingEntities(0.6f)){
                if (!livingEntity.equals(player)){
                    if (livingEntity instanceof ArmorStand){
                    }else{

                        Misc.damageNoTicks(livingEntity,amountOfArmorStands.get(uuid)*12, player);
                        player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);

                        i+=256;
                    }
                }
            }
        }
    }

    private void runEveryTickForPlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Your code here
                    UUID uuid = player.getUniqueId();

                    player.sendActionBar(
                        net.kyori.adventure.text.Component.text(
                            "Cooldown: " + newSwordCooldown.getOrDefault(uuid, 0f)
                            + " | Stands: " + amountOfArmorStands.getOrDefault(uuid, 0f)
                        )
                    );
                }
            }
        }.runTaskTimer(plugin, 0, 1); // 0 delay, run every tick
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
