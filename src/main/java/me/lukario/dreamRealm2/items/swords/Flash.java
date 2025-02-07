package me.lukario.dreamRealm2.items.swords;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
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
import java.util.UUID;

public class Flash implements Listener {

    private final Plugin plugin;

    public Flash(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Flash";
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating speed";
    private static final Material ITEM_MATERIAL = Material.GOLDEN_SWORD;

    boolean isUsingFlashSkill = false;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(16);
            meta.addEnchant(Enchantment.SHARPNESS, 10, true);
            meta.addEnchant(Enchantment.LOOTING, 5, true);
            meta.addEnchant(Enchantment.SWEEPING_EDGE, 5, true);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void flashUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!isHoldingTheCorrectItem(player)){return;}

        if(Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){


            if (isUsingFlashSkill){return;}

            flashSkill(player);
            isUsingFlashSkill = true;

        }
    }

    private void flashSkill(Player player){
        new BukkitRunnable(){
            int runTime = 0;
            @Override
            public void run(){
                if (runTime >= 200){
                    this.cancel();
                    isUsingFlashSkill = false;
                }
                if (!isUsingFlashSkill){return;}

                Location location = player.getEyeLocation();
                Vector direction = location.getDirection();

                if (runTime%2==0){
                    for (float i =0; i <= 64; i+= 0.5f){

                        Location currentLocation = location.clone().add(direction.clone().multiply(i));
                        for (LivingEntity livingEntity : currentLocation.getNearbyLivingEntities(0.5)){
                            if (!livingEntity.equals(player)){
                                player.teleport(currentLocation);
                                livingEntity.damage(64,player);
                                currentLocation.getWorld().spawnParticle(Particle.SWEEP_ATTACK, currentLocation, 1, 0, 0, 0, 0);
                            }
                        }
                    }
                }
                runTime++;
            }
        }.runTaskTimer(plugin,0,1L);
    }


    private static boolean isHoldingTheCorrectItem(Player player) {
        return isCorrectItem(player.getInventory().getItemInMainHand()) ||
               isCorrectItem(player.getInventory().getItemInOffHand());
    }

    private static boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME))) {
            return false;
        }

        return meta.getLore() != null && meta.getLore().contains(ITEM_LORE);
    }
}
