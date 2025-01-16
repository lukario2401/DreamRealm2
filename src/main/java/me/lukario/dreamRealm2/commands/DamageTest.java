package me.lukario.dreamRealm2.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DamageTest implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("Usage: /damagetest <mob> <size> <spacing>");
            return true;
        }

        Player player = (Player) sender;

        // Parse mob type
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid mob type! Check mob types here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html");
            return true;
        }

        // Parse size and spacing
        int size;
        double spacing;
        try {
            size = Integer.parseInt(args[1]);
            spacing = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Size and spacing must be valid numbers!");
            return true;
        }

        // Ensure the entity is a living entity
        if (!entityType.isAlive()) {
            player.sendMessage("Only living entities can be spawned!");
            return true;
        }

        // Summon mobs in a square pattern
        Location startLocation = player.getLocation();
        summonMobsInSquare(player, entityType, size, spacing, startLocation);

        player.sendMessage("Successfully summoned " + entityType.name() + " in a " + size + "x" + size + " square!");
        return true;
    }

    private void summonMobsInSquare(Player player, EntityType entityType, int size, double spacing, Location startLocation) {
        // Adjust start location to center the square
        double offset = (size - 1) * spacing / 2.0;

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                // Calculate spawn location
                double spawnX = startLocation.getX() - offset + (x * spacing);
                double spawnZ = startLocation.getZ() - offset + (z * spacing);
                Location spawnLocation = new Location(startLocation.getWorld(), spawnX, startLocation.getY(), spawnZ);

                // Summon mob without AI
                LivingEntity mob = (LivingEntity) player.getWorld().spawnEntity(spawnLocation, entityType);
                mob.setAI(false); // Disable AI
                mob.setGravity(false); // Optional: Prevent them from falling
                mob.setCollidable(false); // Optional: Prevent collision
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            // Suggest mob types
            List<String> mobs = new ArrayList<>();
            for (EntityType type : EntityType.values()) {
                if (type.isAlive()) {
                    mobs.add(type.name().toLowerCase());
                }
            }
            return mobs;
        }
        return null;
    }
}
