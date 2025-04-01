package me.lukario.dreamRealm2.items.gui;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class GUIItem implements Listener {

    private final Plugin plugin;

    public GUIItem(Plugin plugin) {
     this.plugin = plugin;
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "GUIItem";
    private static final String ITEM_LORE = ChatColor.YELLOW + "##########";
    private static final Material ITEM_MATERIAL = Material.BOOK;

    private HashMap<UUID,Boolean> canFly = new HashMap<>();

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
         meta.setDisplayName(ITEM_NAME);
         meta.setLore(Arrays.asList(ITEM_LORE));
         meta.setUnbreakable(true);
         item.setItemMeta(meta);
        }
        return item;
    }

     @EventHandler
    public void onRightClickGUIItem(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!isHoldingTheCorrectItem(event.getPlayer())){return;}

                openMainGUI(event.getPlayer());
                event.setCancelled(true);

        }
    }

    private void openMainGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8Magical Interface");

        // Add items to GUI
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta meta = infoItem.getItemMeta();
        meta.setDisplayName("§6Information");
        infoItem.setItemMeta(meta);

        ItemStack teleportItem = new ItemStack(Material.ENDER_PEARL);
        ItemMeta tMeta = teleportItem.getItemMeta();
        tMeta.setDisplayName("§bRandom Teleport");
        teleportItem.setItemMeta(tMeta);

        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta cMeta = closeItem.getItemMeta();
        cMeta.setDisplayName("§cClose Menu");
        closeItem.setItemMeta(cMeta);

        ItemStack getFreja = new ItemStack(Material.CHEST);
        ItemMeta fMeta = closeItem.getItemMeta();
        fMeta.setDisplayName("§aGet");
        getFreja.setItemMeta(fMeta);

        ItemStack teleport = new ItemStack(Material.ENDER_EYE);
        ItemMeta teMeta = closeItem.getItemMeta();
        teMeta.setDisplayName("§a Teleport Locations ");
        teleport.setItemMeta(teMeta);

        // Set items in slots
        gui.setItem(11, infoItem);
        gui.setItem(13, teleportItem);
        gui.setItem(15, closeItem);
        gui.setItem(18, teleport);
        gui.setItem(22, getFreja);

        player.openInventory(gui);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§8Magical Interface")) {
            event.setCancelled(true); // Prevent taking items

            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();

            if (clicked == null) return;

            switch (clicked.getType()) {
                case BOOK:
                    player.sendMessage("§eThis is a magical information book!");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    break;

                case ENDER_PEARL:
                    player.teleport(player.getLocation().add(Math.random()*10-5, 0, Math.random()*10-5));
                    player.sendMessage("§bWhoosh! Random teleport!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    break;

                case BARRIER:
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
                    break;

                case CHEST:
                    player.closeInventory();
                    getOpenGUI(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
                    break;


                case ENDER_EYE:
                    player.closeInventory();
                    TeleportGUI.teleportGUI(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
                    break;
            }
        }
    }

    private void getOpenGUI(Player player){
        Inventory inventory = Bukkit.createInventory(null,54,"Get");

        ItemStack dimension = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = dimension.getItemMeta();

        meta.setDisplayName("§aa§bb§cc§dd§ee§ff§gg§hh§ii§jj§kk§ll§mm");
        meta.setCustomModelData(1011);
        meta.setLore(Arrays.asList("Transcend?"));
        dimension.setItemMeta(meta);

        ItemStack backDimension = new ItemStack(Material.ENDER_EYE);
        ItemMeta bdmeta = backDimension.getItemMeta();

        bdmeta.setDisplayName("§aa§bb§cc§dd§ee§ff§gg§hh§ii§jj§kk§ll§mm");
        bdmeta.setCustomModelData(1012);
        bdmeta.setLore(Arrays.asList("Cant handle it?"));
        backDimension.setItemMeta(bdmeta);

        ItemStack paneGray = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta pdmeta = paneGray.getItemMeta();

        pdmeta.setDisplayName("§kk");
        pdmeta.setCustomModelData(1013);
        paneGray.setItemMeta(pdmeta);

        ItemStack paneRed = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        pdmeta.setDisplayName("§kk");
        pdmeta.setCustomModelData(1014);
        paneGray.setItemMeta(pdmeta);

        ItemStack paneBlue = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        pdmeta.setDisplayName("§k");
        pdmeta.setCustomModelData(1015);
        paneGray.setItemMeta(pdmeta);

        ItemStack paneGreen = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        pdmeta.setDisplayName("§k");
        pdmeta.setCustomModelData(1015);
        paneGray.setItemMeta(pdmeta);

        new BukkitRunnable() {
            private float i = 0;

            @Override
            public void run() {
                if (i == 0) {
                    inventory.setItem(4, paneRed);
                } else if (i == 1) {
                    inventory.setItem(4, paneGreen);
                } else if (i == 2) {
                    inventory.setItem(4, paneBlue);
                }

                i += 1;
                if (i >= 3) {
                    i = 0;
                }
            }
        }.runTaskTimer(plugin, 0, 10);

        inventory.setItem(0,paneGray);
        inventory.setItem(1,paneGray);
        inventory.setItem(2,paneGray);
        inventory.setItem(3,paneGray);
        inventory.setItem(5,paneGray);
        inventory.setItem(6,paneGray);
        inventory.setItem(7,paneGray);
        inventory.setItem(8,paneGray);

        inventory.setItem(22,backDimension);
        inventory.setItem(31,dimension);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClickGetGui(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Get")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (item == null){return;}

            if (item.getItemMeta().getCustomModelData()==1011){
                canFly.put(player.getUniqueId(),true);
                preventFall(player);
            }
            if (item.getItemMeta().getCustomModelData()==1012){
                canFly.put(player.getUniqueId(),false);
            }

        }
    }

    private void preventFall(Player player){
        Location previousPlayerLocation = player.getLocation();
        Location location = new Location(player.getWorld(),0,1320,0);
        player.teleport(location);
        new BukkitRunnable(){
            Boolean runTime = true;
            @Override
            public void run(){
                if (runTime==false){
                    player.teleport(previousPlayerLocation);
                    this.cancel();
                }
                if (!player.isOnGround()){
                    if (player.getVelocity().getY()<0){
                        if (player.getFallDistance()>0){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,20,255));
                            player.setVelocity(new Vector(0,0.1,0));
                        }
                    }
                }
                if (canFly.get(player.getUniqueId())!=null){
                    runTime=canFly.get(player.getUniqueId());
                }

            }
        }.runTaskTimer(plugin,0,1);
    }

    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        if (isCorrectItem(mainHandItem)) {
         return true;
        } else return isCorrectItem(offHandItem);
    }

    private static boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        if (!ChatColor.stripColor(meta.getDisplayName()).equals(ChatColor.stripColor(ITEM_NAME))) {
         return false;
        }

        if (meta.getLore() == null) return false;

        for (String loreLine : meta.getLore()) {
         if (ChatColor.stripColor(loreLine).equals(ChatColor.stripColor(ITEM_LORE))) {
             return true;
         }
        }
        return false;
    }
}