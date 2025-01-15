package me.lukario.dreamRealm2;

import org.bukkit.ChatColor;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EntityListener implements Listener {

    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        // Check if the entity is a Cow before casting
        if (event.getRightClicked() instanceof Cow) {
            Cow cow = (Cow) event.getRightClicked();
            // Create an explosion at the cow's location
            cow.getWorld().createExplosion(cow.getLocation(), 11.5F);
        }
    }

    @EventHandler
    public void onEntityDie(EntityDeathEvent event) {
        // Check if the entity is a Cow before casting
        if (event.getEntity() instanceof Cow) {
            Cow cow = (Cow) event.getEntity();
            // Create an explosion at the cow's location
            cow.getWorld().createExplosion(cow.getLocation(), 11.5F, true);
        }
    }

    @EventHandler
    public void onEntityDieCreeper(EntityDeathEvent event) {
        // Check if the entity is a Creeper before casting
        if (event.getEntity() instanceof Creeper) {
            Creeper creeper = (Creeper) event.getEntity();
            // Create an explosion at the creeper's location
            creeper.getWorld().createExplosion(creeper.getLocation(), 11.5F, true);
        }
    }

    private static final double LIFE_STEAL_PERCENTAGE = 1.25; // 125% of the damage dealt is stolen as health

    @EventHandler
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            double damage = event.getFinalDamage(); // Get the final damage dealt to the target

            // Calculate the amount of health to steal
            double lifeStealAmount = damage * LIFE_STEAL_PERCENTAGE;

            // Heal the player by the calculated amount
            double newHealth = Math.min(player.getHealth() + lifeStealAmount, player.getMaxHealth());
            player.setHealth(newHealth);

            // Notify the player (optional)
            player.sendMessage(ChatColor.GREEN + "You stole " + ChatColor.RED + String.format("%.1f", lifeStealAmount) + ChatColor.GREEN + " health!");
        }
    }

}
