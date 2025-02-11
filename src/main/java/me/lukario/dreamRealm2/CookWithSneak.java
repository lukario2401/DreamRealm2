package me.lukario.dreamRealm2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CookWithSneak implements Listener {

    @EventHandler
    private void playerSneakToRegainSaturation(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();

        if (player.isSneaking()){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,1,255, true));
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null && item.getType() == Material.PORKCHOP) {

                    item.setAmount(item.getAmount() - 1);
                    player.getInventory().addItem(ItemStack.of(Material.COOKED_PORKCHOP));

            }
        }
        if (player.isSneaking()) {
            ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null && item.getType() == Material.BEEF) {

                    item.setAmount(item.getAmount() - 1);
                    player.getInventory().addItem(ItemStack.of(Material.COOKED_BEEF));

            }
        }
        if (player.isSneaking()) {
            ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null && item.getType() == Material.CHICKEN) {

                    item.setAmount(item.getAmount() - 1);
                    player.getInventory().addItem(ItemStack.of(Material.COOKED_CHICKEN));

            }
        }
        if (player.isSneaking()) {
            ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null && item.getType() == Material.MUTTON) {

                    item.setAmount(item.getAmount() - 1);
                    player.getInventory().addItem(ItemStack.of(Material.COOKED_MUTTON));

            }
        }
    }
}
