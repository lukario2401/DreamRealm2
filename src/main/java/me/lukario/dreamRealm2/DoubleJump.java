package me.lukario.dreamRealm2;

import org.bukkit.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class DoubleJump implements Listener {


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isOnGround()) {
            if (isBlockAboveHeadAir(player) == Material.AIR) {
                player.setAllowFlight(true);
            }else{
                player.setAllowFlight(false);
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();


        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            if (!player.isOnGround()) {
                event.setCancelled(true);

                // Launch the player upward and forward
                Vector velocity = player.getLocation().getDirection().multiply(2).setY(2);
                player.setVelocity(velocity);

                // Play a sound for the double jump
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);

//                CustomSword.createCustomSword();

                // Reset their flight status
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }

    }

    @EventHandler
public void onPlayerSneakInAir(PlayerToggleSneakEvent event) {
    Player player = event.getPlayer();
    float pitch = player.getLocation().getPitch();

    // Check if the player is not allowed to fly and is in Survival or Adventure mode
    if (!player.getAllowFlight() &&
        (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {

        // Check if the player is in the air (not on the ground)
        if (!player.isOnGround()) {

            // Determine the player's pitch and apply different behaviors
            if (pitch <= 90.0) {
                // Launch the player upward and forward
                Vector velocity = player.getLocation().getDirection().multiply(6).setY(1);
                player.setVelocity(velocity);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0f, 1.0f);

                // Spawn firework particles
                player.getWorld().spawnParticle(
                    Particle.FIREWORK,  // Correct particle type
                    player.getLocation(),      // Player's location
                    50,                        // Number of particles
                    0.5, 0.1, 0.5,             // Spread (X, Y, Z)
                    0                          // Speed
                );
            } else {
                // Push the player downward
                Vector velocity = new Vector(0, -2, 0);
                player.setVelocity(velocity);

                // Play a dragon flap sound
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0f, 1.0f);
            }
        }
    }
}
    public Material isBlockAboveHeadAir(Player player) {
    // Get the block two blocks above the player's current feet position
    Location blockAboveHead = player.getLocation().clone();
    blockAboveHead.setY(player.getLocation().getBlockY() + 2);

    // Check if the block is air

    boolean isAir = blockAboveHead.getBlock().getType() == Material.AIR;

    // Debug message to indicate the block type
//    player.sendMessage("Block above your head is: " + blockAboveHead.getBlock().getType());

    return blockAboveHead.getBlock().getType();
}

    @EventHandler
    public void onPlayerMovee(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(player.getGameMode()==GameMode.SURVIVAL||player.getGameMode()==GameMode.ADVENTURE){
            if (player.isFlying()) {
                player.setFlying(false);
            }
        }
    }

}
