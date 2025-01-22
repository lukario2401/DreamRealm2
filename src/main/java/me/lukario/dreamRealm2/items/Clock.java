package me.lukario.dreamRealm2.items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class Clock implements Listener {
    private final Plugin plugin;

    public Clock(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final String ITEM_NAME = net.md_5.bungee.api.ChatColor.of("#e668c6") + "Clock";//D88F07
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Time";
    private static final Material ITEM_MATERIAL = Material.CLOCK;

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ITEM_NAME);
            meta.setLore(Arrays.asList(ITEM_LORE));
            meta.setUnbreakable(true);
            meta.setCustomModelData(10);
            meta.addEnchant(Enchantment.SHARPNESS,10,true);
            meta.addEnchant(Enchantment.LOOTING,5,true);
            meta.addEnchant(Enchantment.SWEEPING_EDGE,5,true);
            meta.addEnchant(Enchantment.FIRE_ASPECT,5,true);
            item.setItemMeta(meta);

        }
        return item;
    }

    @EventHandler
    public void timeStop(PlayerInteractEvent event){
        Player player = event.getPlayer();


        if (!isHoldingTheCorrectItem(player)){return;}
        if (player.hasCooldown(createItem())){
            player.sendMessage("This item is on cooldown!");
            event.setCancelled(true);
            return;
        }
        if (Action.RIGHT_CLICK_AIR==event.getAction()){

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tick freeze");
            player.setCooldown(createItem(), 100);

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tick unfreeze");
                }
            }.runTaskLater(plugin, 100L);

            for (LivingEntity livingEntity : player.getLocation().getNearbyLivingEntities(6)){
                if (!livingEntity.equals(player)){

                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,100,255));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,100,255));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,100,255));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,100,255));

                }
            }
        }
    }


    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Check if either hand holds the correct item
        if (isCorrectItem(mainHandItem)) {
            return true; // Correct item in main hand
        } else if (isCorrectItem(offHandItem)) {
            return true; // Correct item in off hand
        }

        return false; // No correct item in either hand
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
