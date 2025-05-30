package me.lukario.dreamRealm2.items.guns_and_crates;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Medkit implements Listener {

    // Map each MedKit‐item’s UUID to the set of slots it’s already used
    private static final Map<UUID, Set<Integer>> clickedSlotsByItem = new HashMap<>();
    private final Plugin plugin;
    private static NamespacedKey medkitKey = null;

    private static final String ITEM_NAME = ChatColor.GOLD + "MedKit";
    private static final String ITEM_LORE = ChatColor.YELLOW + "##########";
    private static final Material ITEM_MATERIAL = Material.COPPER_INGOT;

    public Medkit(Plugin plugin) {
        this.plugin = plugin;
        // key for storing the MedKit UUID on the ItemStack
        this.medkitKey = new NamespacedKey(plugin, "medkit-id");
    }

    /** Call this to give a player a fresh MedKit */
    public void giveMedkit(Player player) {
        player.getInventory().addItem(createItem());
    }

    /** Creates an ItemStack stamped with a unique UUID in its PDC */
    public static ItemStack createItem() {
        ItemStack item = new ItemStack(ITEM_MATERIAL);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // generate and store a new UUID for this instance
        UUID id = UUID.randomUUID();
        meta.getPersistentDataContainer()
            .set(medkitKey, PersistentDataType.STRING, id.toString());

        meta.setDisplayName(ITEM_NAME);
        meta.setLore(Collections.singletonList(ITEM_LORE));
        meta.setUnbreakable(true);
        meta.setCustomModelData(2);
        item.setItemMeta(meta);

        // prepare an empty set so clicks can be tracked
        clickedSlotsByItem.put(id, new HashSet<>());
        return item;
    }

    /** Opens the GUI, carrying the item’s UUID in the holder */
    public void openGUI(Player player, UUID medkitId) {
        MedkitHolder holder = new MedkitHolder(medkitId);
        Inventory gui = Bukkit.createInventory(holder, 9, ChatColor.DARK_AQUA + "MedKit Menu");

        Set<Integer> used = clickedSlotsByItem.getOrDefault(medkitId, new HashSet<>());

        for (int i = 0; i < 9; i++) {
            ItemStack button;
            ItemMeta bm;
            if (used.contains(i)) {
                button = new ItemStack(Material.BARRIER);
                bm = button.getItemMeta();
                bm.setDisplayName(ChatColor.RED + "Used");

            } else {
                button = new ItemStack(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
                bm = button.getItemMeta();
                bm.setDisplayName(ChatColor.GREEN + "Ability " + (i + 1));
            }
            button.setItemMeta(bm);
            gui.setItem(i, button);
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        ItemStack held = event.getItem();
        if (!isCorrectItem(held)) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // extract the medkit-id from the item
            String idString = held.getItemMeta()
                                 .getPersistentDataContainer()
                                 .get(medkitKey, PersistentDataType.STRING);
            if (idString == null) return;

            UUID medkitId = UUID.fromString(idString);
            openGUI(event.getPlayer(), medkitId);
            event.setCancelled(true);
        }
    }

@EventHandler
public void onClick(InventoryClickEvent e) {
    if (!(e.getInventory().getHolder() instanceof MedkitHolder)) return;
    e.setCancelled(true);

    MedkitHolder holder = (MedkitHolder) e.getInventory().getHolder();
    UUID medkitId = holder.getMedkitId();
    int slot = e.getRawSlot();
    Player player = (Player) e.getWhoClicked();

    // only care about the 0–8 “buttons”
    if (slot < 0 || slot > 8) return;

    Set<Integer> used = clickedSlotsByItem.computeIfAbsent(medkitId, k -> new HashSet<>());
    if (used.contains(slot)) {
        player.sendMessage(ChatColor.RED + "You already used this ability!");
        return;
    }

    // mark as used
    used.add(slot);

    // perform the slot’s ability
    switch (slot) {
        case 0:
            break;
        case 1:
            player.sendMessage("1");
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 4));

            break;
        case 2:
            player.sendMessage("2");
            player.getInventory().addItem(new ItemStack(Material.MILK_BUCKET));

            break;
        case 3:
            player.sendMessage("3");
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,120,1,false,false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,120,1,false,false));

            break;
        case 4:
            player.sendMessage("4");
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,120,1,false,false));

            break;
        case 5:
            player.sendMessage("5");
            player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH,5,2,false,false));

            break;
        case 6:
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,600,1,false,false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600,1,false,false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,600,1,false,false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING,600,1,false,false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,600,1,false,false));

            new BukkitRunnable(){
                @Override
                public void run(){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,1200,1,false,false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,1200,1,false,false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE,1200,1,false,false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA,1200,1,false,false));

                }
            }.runTaskTimer(plugin,600,0);
            break;
        case 7:
            player.sendMessage("7");

            ItemStack potion = new ItemStack(Material.LINGERING_POTION);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();

            meta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1), true);

            potion.setItemMeta(meta);

            player.getInventory().addItem(potion);

            break;
        case 8:
            break;
        default:
            return;
    }

    // feedback
//    player.sendMessage(ChatColor.YELLOW + "Activated ability " + (slot + 1));

    // disable the button visually
    ItemStack disabled = new ItemStack(Material.BARRIER);
    ItemMeta dm = disabled.getItemMeta();
    dm.setDisplayName(ChatColor.RED + "Used");
    disabled.setItemMeta(dm);
    e.getInventory().setItem(slot, disabled);
}



    /** Checks display name + lore + material */
    private boolean isCorrectItem(ItemStack item) {
        if (item == null || item.getType() != ITEM_MATERIAL) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getLore() == null) return false;
        if (!meta.getDisplayName().equals(ITEM_NAME)) return false;
        return meta.getLore().contains(ITEM_LORE);
    }

    /** Custom holder so we know which medkit instance opened this GUI */
    private static class MedkitHolder implements InventoryHolder {
        private final UUID medkitId;
        MedkitHolder(UUID medkitId) { this.medkitId = medkitId; }
        public UUID getMedkitId() { return medkitId; }
        @Override
        public Inventory getInventory() { return null; }
    }
}
