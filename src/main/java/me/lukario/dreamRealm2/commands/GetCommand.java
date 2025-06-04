package me.lukario.dreamRealm2.commands;

import me.lukario.dreamRealm2.items.guns_and_crates.Medkit;

import me.lukario.dreamRealm2.items.armor.Ferocity;
import me.lukario.dreamRealm2.items.gui.GUIItem;
import me.lukario.dreamRealm2.items.guns_and_crates.Medkit;
import me.lukario.dreamRealm2.items.guns_and_crates.guns.Ak;
import me.lukario.dreamRealm2.items.guns_and_crates.guns.Glock;
import me.lukario.dreamRealm2.items.special.builder.*;
import me.lukario.dreamRealm2.items.special.magic.*;
import me.lukario.dreamRealm2.items.special.ranged.bow.*;
import me.lukario.dreamRealm2.items.special.ranged.misc.*;
import me.lukario.dreamRealm2.items.special.Clock;
import me.lukario.dreamRealm2.items.special.JetSu;
import me.lukario.dreamRealm2.items.swords.Claws;
import me.lukario.dreamRealm2.items.swords.GiantSword;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.lukario.dreamRealm2.items.special.magic.WizardWand.createCustomWizardWand;
import static me.lukario.dreamRealm2.items.swords.CustomSword.createCustomSword;

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

        if (args.length == 1 && args[0].equalsIgnoreCase("Guide")) {
            player.getInventory().addItem(GUIItem.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Guide");
            return true;
        }

        if (!player.isOp()) {
            sender.sendMessage(ChatColor.RED + "You need to be an operator to use this command");
            return true;
        }
        // Handle "launch sword"
        if (args.length == 2 && args[0].equalsIgnoreCase("launch") && args[1].equalsIgnoreCase("sword")) {
            player.getInventory().addItem(createCustomSword());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Launch Sword!");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("Shadow") && args[1].equalsIgnoreCase("Dance")) {
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.ShadowDance.createShadowDance());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Shadow Dance!");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("Wizard") && args[1].equalsIgnoreCase("Wand")) {
            player.getInventory().addItem(createCustomWizardWand());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Wizard Wand!");
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("Dual") && args[1].equalsIgnoreCase("Wield")) {
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.DualWield.createDualWield());
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
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Katana.createItem());
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Hyperion")) {
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Hyperion!");
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Hyperion.createHyperion());
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
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.SinOfSolace.createSinOfSolace());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Sin Of Solace!");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Rapier")) {
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Rapier.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Rapier");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Scythe")) {
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Scythe.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Scythe");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Dagger")) {
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Dagger.createItem());
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
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Flash.createItem());
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
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Slash.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Slash");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Style")) {
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Style.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Style");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Swipe")) {
            player.getInventory().addItem(me.lukario.dreamRealm2.items.swords.Swipe.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Swipe");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("MidasStaff")) {
            player.getInventory().addItem(MidasStaff.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "MidasStaff");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("YetiSword")) {
            player.getInventory().addItem(YetiSword.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "YetiSword");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Meteor")) {
            player.getInventory().addItem(Meteor.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Meteor");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Link")) {
            player.getInventory().addItem(Link.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Link");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Swift")) {
            player.getInventory().addItem(Swift.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Swift");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Satellite")) {
            player.getInventory().addItem(Satellite.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Satellite");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Termination")) {
            player.getInventory().addItem(Terminator.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Terminator");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Claws")) {
            player.getInventory().addItem(Claws.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Claws");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Missile")) {
            player.getInventory().addItem(Missile.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Missile");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("GraveYard")) {
            player.getInventory().addItem(GraveYard.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "GraveYard");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("SphereCage")) {
            player.getInventory().addItem(SphereCage.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "SphereCage");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("FireCracker")) {
            player.getInventory().addItem(FireCracker.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "FireCracker");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Stack")) {
            player.getInventory().addItem(Stack.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Stack");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Freja")) {
            player.getInventory().addItem(Freja.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Freja");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("AirStrike")) {
            player.getInventory().addItem(Jet.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "AirStrike");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("CAS")) {
            player.getInventory().addItem(CAS.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "CAS");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("LaserEye")) {
            player.getInventory().addItem(LaserEye.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "LaserEye");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Mech")) {
            player.getInventory().addItem(Mech.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Mech");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("MedKit")) {
            ItemStack medkitItem = Medkit.createItem();

            player.getInventory().addItem(Medkit.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Mech");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Glock")) {
            player.getInventory().addItem(Glock.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Glock");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("ammo_9mm")) {
            for (int i = 0; i < 5; i++){
                ItemStack item = new ItemStack(Material.COPPER_INGOT);
                ItemMeta metaa = item.getItemMeta();
                metaa.setCustomModelData(2);
                item.setItemMeta(metaa);

                player.getInventory().addItem(item);
            }

            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("Ak47")) {
            player.getInventory().addItem(Ak.createItem());
            player.sendMessage(ChatColor.GREEN + "You have received the " + ChatColor.GOLD + "Ak47");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("ammo_7.62")) {
            for (int i = 0; i < 4; i++){
                ItemStack item = new ItemStack(Material.COPPER_INGOT);
                ItemMeta metaa = item.getItemMeta();
                metaa.setCustomModelData(3);
                item.setItemMeta(metaa);

                player.getInventory().addItem(item);
            }

            return true;
        }


        // Invalid argument
        player.sendMessage(ChatColor.RED + "Unknown item type. Use /get <launch sword|cat>");
        return false;
    }

   @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();
        completions.add("Guide");

        if (!player.isOp()) {
            sender.sendMessage(ChatColor.RED + "You need to be an operator to use this command");
            return completions;
        }

        // Define all possible completions for the first argument
        List<String> mainTypes = Arrays.asList(
            "Launch", "Tornado", "Shuriken", "Cat", "Jet", "Rapier", "Card", "Theater", "Fence", "ChainedBuff",
            "Style", "YetiSword", "Link", "Wizard", "Clock", "Dagger", "SinOfSolace", "Scythe", "Terminator", "ShareHealth",
            "Chain", "Slash", "Swipe", "MidasStaff", "Shadow", "Dual", "Swift", "Satellite", "Pyromancer", "GiantSword",
            "Flame", "Ferocity", "Katana", "Wrench", "Flash", "FireWand", "Portal", "Meteor", "Termination", "Claws",
            "Missile", "GraveYard", "SphereCage", "FireCracker", "Stack", "Freja","AirStrike", "CAS", "LaserEye", "Mech","Medkit",
                "Glock", "ammo", "Ak47"
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
