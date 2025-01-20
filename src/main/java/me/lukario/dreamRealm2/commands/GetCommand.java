package me.lukario.dreamRealm2.commands;

import me.lukario.dreamRealm2.items.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.lukario.dreamRealm2.items.CustomSword.createCustomSword;
import static me.lukario.dreamRealm2.items.WizardWand.createCustomWizardWand;

public class GetCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Ensure the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Validate arguments
        if (args.length == 0 || args.length > 2) {
            player.sendMessage(ChatColor.RED + "Usage: /get <type> [item]");
            return false;
        }

        // Handle "launch sword"
        if (args.length == 2 && args[0].equalsIgnoreCase("launch") && args[1].equalsIgnoreCase("sword")) {
            player.getInventory().addItem(createCustomSword());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Launch Sword!");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("Shadow") && args[1].equalsIgnoreCase("Dance")) {
            player.getInventory().addItem(ShadowDance.createShadowDance());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Shadow Dance!");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("Wizard") && args[1].equalsIgnoreCase("Wand")) {
            player.getInventory().addItem(createCustomWizardWand());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Wizard Wand!");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("Dual") && args[1].equalsIgnoreCase("Wield")) {
            player.getInventory().addItem(DualWield.createDualWield());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Dual Wield!");
            return true;
        }

        // Handle "cat"
        if (args.length == 1 && args[0].equalsIgnoreCase("Cat")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Cat!");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Pyromancer")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Pyromancer!");
            player.getInventory().addItem(Pyromancer.createPyromancer());
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("GiantSword")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Gaint Sword!");
            player.getInventory().addItem(GiantSword.createItemGiantSword());
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Hyperion")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Hyperion!");
            player.getInventory().addItem(Hyperion.createHyperion());
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("JujuShortbow")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Juju ShortBow!");
            player.getInventory().addItem(JujuShortBow.createJujuShortBow());
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Terminator")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Terminator!");
            player.getInventory().addItem(Arch.createTerminator());
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("SinOfSolace")) {
            player.getInventory().addItem(SinOfSolace.createSinOfSolace());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Sin Of Solace!");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Rapier")) {
            player.getInventory().addItem(Rapier.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Rapier");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Scythe")) {
            player.getInventory().addItem(Scythe.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Scythe");
            return true;
        }

        // Invalid argument
        player.sendMessage(ChatColor.RED + "Unknown item type. Use /get <launch sword|cat>");
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Suggest main types
            completions.addAll(Arrays.asList("launch", "cat","Rapier", "wizard","SinOfSolace","Scythe","Terminator","Shadow","Dual","Pyromancer","GiantSword"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("launch")) {
            // Suggest items for "launch"
            completions.add("sword");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("wizard")) {
            // Suggest items for "launch"
            completions.add("wand");
        }else if (args.length == 2 && args[0].equalsIgnoreCase("Shadow")) {
            // Suggest items for "launch"
            completions.add("Dance");
        }else if (args.length == 2 && args[0].equalsIgnoreCase("Dual")) {
            // Suggest items for "launch"
            completions.add("Wield");
        }

        return completions;
    }
}
