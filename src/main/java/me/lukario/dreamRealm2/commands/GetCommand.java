package me.lukario.dreamRealm2.commands;

import me.lukario.dreamRealm2.items.armor.Ferocity;
import me.lukario.dreamRealm2.items.special.builder.Fence;
import me.lukario.dreamRealm2.items.special.builder.Portal;
import me.lukario.dreamRealm2.items.special.builder.Wrench;
import me.lukario.dreamRealm2.items.special.magic.*;
import me.lukario.dreamRealm2.items.special.ranged.bow.Arch;
import me.lukario.dreamRealm2.items.special.ranged.bow.JujuShortBow;
import me.lukario.dreamRealm2.items.special.ranged.bow.TornadoBow;
import me.lukario.dreamRealm2.items.special.ranged.misc.Cards;
import me.lukario.dreamRealm2.items.special.Clock;
import me.lukario.dreamRealm2.items.special.JetSu;
import me.lukario.dreamRealm2.items.special.ranged.misc.FlameThrower;
import me.lukario.dreamRealm2.items.special.ranged.misc.Pyromancer;
import me.lukario.dreamRealm2.items.special.ranged.misc.Shuriken;
import me.lukario.dreamRealm2.items.swords.*;
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

import static me.lukario.dreamRealm2.items.swords.CustomSword.createCustomSword;
import static me.lukario.dreamRealm2.items.special.magic.WizardWand.createCustomWizardWand;

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
        if (args.length == 2 && args[0].equalsIgnoreCase("Tornado") && args[1].equalsIgnoreCase("Bow")) {
            player.getInventory().addItem(TornadoBow.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Tornado Bow!");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("Flame") && args[1].equalsIgnoreCase("Thrower")) {
            player.getInventory().addItem(FlameThrower.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "FlameThrower!");
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
        if (args.length == 1 && args[0].equalsIgnoreCase("Ferocity")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Ferocity!");

            player.getInventory().addItem(Ferocity.createItem());
            player.getInventory().addItem(Ferocity.createItemChestplate());
            player.getInventory().addItem(Ferocity.createItemLeggings());
            player.getInventory().addItem(Ferocity.createItemBoots());

            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("GiantSword")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Gaint Sword!");
            player.getInventory().addItem(GiantSword.createItemGiantSword());
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Katana")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Katana!");
            player.getInventory().addItem(Katana.createItem());
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
        if (args.length == 1 && args[0].equalsIgnoreCase("Dagger")) {
            player.getInventory().addItem(Dagger.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Dagger");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Clock")) {
            player.getInventory().addItem(Clock.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Clock");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Card")) {
            player.getInventory().addItem(Cards.createItem(11));
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Card");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Jet")) {
            player.getInventory().addItem(JetSu.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Jet");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Shuriken")) {
            player.getInventory().addItem(Shuriken.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Shuriken");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Wrench")) {
            player.getInventory().addItem(Wrench.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Wrench");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Flash")) {
            player.getInventory().addItem(Flash.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Flash");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("FireWand")) {
            player.getInventory().addItem(FireWand.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "FireWand");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Theater")) {
            player.getInventory().addItem(Theather.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Theater");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Fence")) {
            player.getInventory().addItem(Fence.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Fence");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Portal")) {
            player.getInventory().addItem(Portal.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Portal");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("ShareHealth")) {
            player.getInventory().addItem(ShareHealth.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "ShareHealth");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Chain")) {
            player.getInventory().addItem(Chain.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Chain");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("ChainedBuff")) {
            player.getInventory().addItem(ChainedBuff.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "ChainedBuff");
            return true;
        }
            if (args.length == 1 && args[0].equalsIgnoreCase("Slash")) {
            player.getInventory().addItem(Slash.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Slash");
            return true;
        }




        // Invalid argument
        player.sendMessage(ChatColor.RED + "Unknown item type. Use /get <launch sword|cat>");
        return false;
    }

   @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        // Define all possible completions for the first argument
        List<String> mainTypes = Arrays.asList(
            "Launch", "Tornado", "Shuriken", "Cat", "Jet", "Rapier", "Card","Theater","Fence","ChainedBuff",
            "Wizard", "Clock", "Dagger", "SinOfSolace", "Scythe", "Terminator","ShareHealth","Chain","Slash",
            "Shadow", "Dual", "Pyromancer", "GiantSword","Flame","Ferocity","Katana","Wrench","Flash","FireWand","Portal"
        );

        // First argument suggestions
        if (args.length == 1) {
            String currentInput = args[0].toLowerCase(); // Get current input in lowercase
            for (String type : mainTypes) {
                if (type.toLowerCase().startsWith(currentInput)) {
                    completions.add(type); // Add matching types
                }
            }
        }

        // Second argument suggestions based on the first argument
        else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "launch":
                    completions.add("sword");
                    break;
                case "wizard":
                    completions.add("wand");
                    break;
                case "shadow":
                    completions.add("dance");
                    break;
                case "dual":
                    completions.add("wield");
                case "flame":
                    completions.add("thrower");
                case "tornado":
                    completions.add("bow");
                    break;
            }
        }
        return completions;
    }
}
