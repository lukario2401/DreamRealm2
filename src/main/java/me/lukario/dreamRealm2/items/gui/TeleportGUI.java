package me.lukario.dreamRealm2.items.gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class TeleportGUI implements Listener {

    HashMap<UUID, Location> locationOne = new HashMap<>();
    HashMap<UUID, Location> locationTwo = new HashMap<>();
    HashMap<UUID, Location> locationThree = new HashMap<>();

    public static void teleportGUI(Player player){
        if (player==null){return;}
        Inventory inventory = Bukkit.createInventory(player,54,"TeleportGUI");

        ItemStack teleport1 = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = teleport1.getItemMeta();
        meta.setDisplayName("§a Teleport To Location I");
        meta.setCustomModelData(10111);
        meta.setLore(Arrays.asList("Tp location 1"));
        teleport1.setItemMeta(meta);

        ItemStack teleportLocation1 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta bdmeta = teleportLocation1.getItemMeta();
        bdmeta.setDisplayName("§b Set Teleport Location ");
        bdmeta.setCustomModelData(10112);
        bdmeta.setLore(Arrays.asList("Set Teleport Location 1"));
        teleportLocation1.setItemMeta(bdmeta);

        ItemStack teleport2 = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta2 = teleport1.getItemMeta();
        meta2.setDisplayName("§a Teleport To Location II");
        meta2.setCustomModelData(10121);
        meta2.setLore(Arrays.asList("Tp location 2"));
        teleport2.setItemMeta(meta2);

        ItemStack teleportLocation2 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta bdmeta2 = teleportLocation1.getItemMeta();
        bdmeta2.setDisplayName("§b Set Teleport Location ");
        bdmeta2.setCustomModelData(10122);
        bdmeta2.setLore(Arrays.asList("Set Teleport Location 2"));
        teleportLocation2.setItemMeta(bdmeta2);

        ItemStack teleport3 = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta3 = teleport1.getItemMeta();
        meta3.setDisplayName("§a Teleport To Location III");
        meta3.setCustomModelData(10131);
        meta3.setLore(Arrays.asList("Tp location 3"));
        teleport3.setItemMeta(meta3);

        ItemStack teleportLocation3 = new ItemStack(Material.ENDER_PEARL);
        ItemMeta bdmeta3 = teleportLocation1.getItemMeta();
        bdmeta3.setDisplayName("§b Set Teleport Location ");
        bdmeta3.setCustomModelData(10132);
        bdmeta3.setLore(Arrays.asList("Set Teleport Location 3"));
        teleportLocation3.setItemMeta(bdmeta3);

        inventory.setItem(11,teleport1);
        inventory.setItem(20,teleportLocation1);

        inventory.setItem(13,teleport2);
        inventory.setItem(22,teleportLocation2);

        inventory.setItem(15,teleport3);
        inventory.setItem(24,teleportLocation3);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClickGetGuiii(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("TeleportGUI")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (item == null){return;}

            if (item.getItemMeta().getCustomModelData()==10111){
                if (locationOne.get(player.getUniqueId())!=null){
                    player.teleport(locationOne.get(player.getUniqueId()));
                    player.sendMessage("teleported to Location I");
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                }
            }
            if (item.getItemMeta().getCustomModelData()==10112){
                player.sendMessage("Location I has been set");
                locationOne.put(player.getUniqueId(),player.getLocation());
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                player.closeInventory();
            }

            if (item.getItemMeta().getCustomModelData()==10121){
                if (locationTwo.get(player.getUniqueId())!=null){
                    player.teleport(locationTwo.get(player.getUniqueId()));
                    player.sendMessage("teleported to Location II");
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                }
            }
            if (item.getItemMeta().getCustomModelData()==10122){
                locationTwo.put(player.getUniqueId(),player.getLocation());
                player.sendMessage("Location II has been set");
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                player.closeInventory();
            }

            if (item.getItemMeta().getCustomModelData()==10131){
                if (locationThree.get(player.getUniqueId())!=null){
                    player.teleport(locationThree.get(player.getUniqueId()));
                    player.sendMessage("teleported to Location III");
                    player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                }
            }
            if (item.getItemMeta().getCustomModelData()==10132){
                locationThree.put(player.getUniqueId(),player.getLocation());
                player.sendMessage("Location III has been set");
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                player.closeInventory();
            }
        }
    }
}
