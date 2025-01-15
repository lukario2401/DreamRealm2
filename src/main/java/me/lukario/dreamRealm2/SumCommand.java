package me.lukario.dreamRealm2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SumCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Ensure the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Validate arguments
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /sum <amount> <mob>");
            return false;
        }

        // Parse the number of mobs
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "First argument must be a number (amount of mobs).");
            return false;
        }

        // Parse the mob type
        String mobTypeStr = args[1].toUpperCase();
        EntityType mobType;
        try {
            mobType = EntityType.valueOf(mobTypeStr);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Unknown mob type: " + mobTypeStr);
            return false;
        }

        // Summon the mobs
        for (int i = 0; i < amount; i++) {
            LivingEntity mob = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), mobType);
            mob.setCustomName(ChatColor.GREEN + mobType.name() + " #" + (i + 1));
        }

        player.sendMessage(ChatColor.GREEN + "You have summoned " + amount + " " + mobType.name() + "(s)!");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Suggest amounts
            completions.addAll(Arrays.asList("5", "10", "20", "30", "40", "50"));
        } else if (args.length == 2) {
            // Suggest mob types (you can expand this list with more mob types)
            for (EntityType entityType : EntityType.values()) {
                if (entityType.isAlive()) {
                    completions.add(entityType.name().toLowerCase());
                }
            }
        }

        return completions;
    }
}
