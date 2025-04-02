package me.lukario.dreamRealm2.items.gui;

import me.lukario.dreamRealm2.Misc;
import me.lukario.dreamRealm2.items.special.ranged.bow.Freja;
import me.lukario.dreamRealm2.items.special.ranged.misc.GraveYard;
import me.lukario.dreamRealm2.items.swords.Rapier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class ShopGUI implements Listener {

    private final Plugin plugin;

    public ShopGUI(Plugin plugin) {
        this.plugin = plugin;
    }

    private static HashMap<UUID,Double> playersCash = new HashMap<>();

    public static void shopGuiCreate(Player player){
        if (player==null){return;}
        UUID uuid = player.getUniqueId();
        if (playersCash.get(uuid)==null){
            playersCash.put(uuid,0D);
        }
        Inventory inventory = Bukkit.createInventory(player,54,"Shop");

        Misc.createInventoryItem(inventory,Material.CROSSBOW,11,10202,"Freja","Click to buy item Freja");
        Misc.createInventoryItem(inventory,Material.DIAMOND_SWORD,13,10203,"Rapier","Click to buy item Rapier");
        Misc.createInventoryItem(inventory,Material.IRON_SHOVEL,15,10204,"GraveYard","Click to buy item GraveYard");

        String sunFlowerName = "You have: "+playersCash.get(player.getUniqueId()).toString()+" Coins";
        Misc.createInventoryItem(inventory,Material.SUNFLOWER,49,10210,sunFlowerName);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClickInShop(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Shop")) {
            event.setCancelled(true); // Prevent taking items

            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            UUID uuid = player.getUniqueId();

            if (item == null) return;

//            if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

            ItemMeta meta = item.getItemMeta();

            if (meta.hasCustomModelData()){
                if (item.getItemMeta().getCustomModelData()==10202){
                    if (playersCash.get(uuid)>=1200){

                        playersCash.put(uuid,playersCash.get(uuid)-1200);
                        player.getInventory().addItem(Freja.createItem());
                        Misc.createInventoryItem(event.getInventory(),Material.SUNFLOWER,49,10210,"You have: "+playersCash.get(player.getUniqueId()).toString()+" Coins");
                        player.sendMessage("Purchase successful");
                        player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);

                    }else {
                        player.sendMessage("Not enough coins");
                        player.playSound(player,Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                    }
                }
                if (item.getItemMeta().getCustomModelData()==10203){
                    if (playersCash.get(uuid)>=2400){

                        playersCash.put(uuid,playersCash.get(uuid)-2400);
                        player.getInventory().addItem(Rapier.createItem());
                        Misc.createInventoryItem(event.getInventory(),Material.SUNFLOWER,49,10210,"You have: "+playersCash.get(player.getUniqueId()).toString()+" Coins");
                        player.sendMessage("Purchase successful");
                        player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);

                    }else {
                        player.sendMessage("Not enough coins");
                        player.playSound(player,Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                    }
                }
                if (item.getItemMeta().getCustomModelData()==10204){
                    if (playersCash.get(uuid)>=3600){

                        playersCash.put(uuid,playersCash.get(uuid)-3600);
                        player.getInventory().addItem(GraveYard.createItem());
                        Misc.createInventoryItem(event.getInventory(),Material.SUNFLOWER,49,10210,"You have: "+playersCash.get(player.getUniqueId()).toString()+" Coins");
                        player.sendMessage("Purchase successful");
                        player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
                    }else {
                        player.sendMessage("Not enough coins");
                        player.playSound(player,Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
                    }
                }
            }

            if (item.getType()==Material.DIAMOND){

                player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 1));
                playersCash.put(uuid,playersCash.get(uuid)+100);

                player.sendMessage("You have: "+ playersCash.get(uuid) + " Coins");
                Misc.createInventoryItem(event.getInventory(),Material.SUNFLOWER,49,10210,"You have: "+playersCash.get(player.getUniqueId()).toString()+" Coins");
                player.playSound(player,Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);

            }
        }
    }
}
