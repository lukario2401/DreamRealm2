package me.lukario.dreamRealm2.items.special.ranged.misc;

import me.lukario.dreamRealm2.Misc;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class MidasStaff implements Listener {

    private final Plugin plugin;

    public MidasStaff(Plugin plugin) {
        this.plugin = plugin;
        cooldownManagement();
    }

    private static final String ITEM_NAME = ChatColor.of("#D88F07") + "Midas Staff";//e668c6
    private static final String ITEM_LORE = ChatColor.YELLOW + "Crafted after defeating Midas";
    private static final Material ITEM_MATERIAL = Material.GOLDEN_SHOVEL;

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

    private static HashMap<UUID,Float> cooldown = new HashMap<>();

    private void cooldownManagement(){
        new BukkitRunnable(){
            @Override
            public void run(){

                Set<UUID> key = cooldown.keySet();

                for (UUID uuid : key){
                    if (cooldown.get(uuid)>0){
                        cooldown.put(uuid,cooldown.get(uuid)-1);
                    }
                }

            }
        }.runTaskTimer(plugin,0,1);
    }

    @EventHandler
    public void midasStaffUsed(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cooldown.get(uuid)==null){
            cooldown.put(uuid,0f);
        }

        if (!isHoldingTheCorrectItem(player)){return;};
        if (Action.RIGHT_CLICK_AIR==event.getAction()||Action.RIGHT_CLICK_BLOCK==event.getAction()){

            if (cooldown.get(uuid)==0){

                player.playSound(player, Sound.ENTITY_BLAZE_SHOOT,1,1);

                midasStaffFallingBlock(player);

                cooldown.put(uuid,10f);

            }else{
                player.sendMessage(org.bukkit.ChatColor.DARK_RED + "Your Cooldown is: " + (cooldown.get(uuid)/20)+" Seconds");
                player.playSound(player,Sound.ENTITY_ENDERMAN_TELEPORT,1,1);
            }
        }
    }

    private void midasStaffFallingBlock(Player player){

        for (float k = -1; k < 2; k+=1){
            float finalK = k;
            new BukkitRunnable(){

                int i = 0;

                Location location = player.getEyeLocation().add(0,1,0);
                Vector direction = location.getDirection();

                Vector rightOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(finalK); // 0.5 blocks right
                Location current = location.clone();

                {current.add(rightOffset);}

                @Override
                public void run(){
                    if (i>=8){
                        this.cancel();
                    }

                    current = current.clone().add(direction.clone().multiply(1));

                    FallingBlock gold = current.getWorld().spawnFallingBlock(current, Material.GOLD_BLOCK.createBlockData());

                    gold.setDropItem(false);
                    gold.setInvulnerable(true);
                    gold.setGravity(true);
                    gold.setCancelDrop(true);

                    Location goldLocation = gold.getLocation();

                    for (LivingEntity livingEntity : goldLocation.getNearbyLivingEntities(2.5)){
                        if (!livingEntity.equals(player)){
                            Misc.damageNoTicks(livingEntity,21,player);
                        }
                    }
                    i+=1;
                }
            }.runTaskTimer(plugin,0,1);
        }
    }


    private static boolean isHoldingTheCorrectItem(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();

        // Check if either hand holds the correct item
        if (isCorrectItem(mainHandItem)) {
            return true; // Correct item in main hand
        } else if (isCorrectItem(offHandItem)) {
            return true; // Correct item in off hand
        }

        return false; // No correct item in either hand
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

//package me.lukario.dreamRealm2.items.swords;
//
//import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
//import me.lukario.dreamRealm2.Misc;
//import net.md_5.bungee.api.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.Particle;
//import org.bukkit.Sound;
//import org.bukkit.entity.FallingBlock;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.entity.EntityChangeBlockEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.metadata.FixedMetadataValue;
//import org.bukkit.plugin.Plugin;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.util.Vector;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Set;
//import java.util.UUID;
//
//import static me.lukario.dreamRealm2.Misc.damageNoTicks;
//
//public class MidasStaff implements Listener {
//
//    private final Plugin plugin;
//
//    public MidasStaff(Plugin plugin) {
//        this.plugin = plugin;
//        cooldownManagement();
//    }
//
//    // ... [Existing code remains the same until midasStaffFallingBlock method]
//
//    private void midasStaffFallingBlock(Player player) {
//        Location location = player.getEyeLocation();
//        Vector direction = location.getDirection();
//
//        for (float k = -1; k < 2; k += 1) {
//            Vector rightOffset = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(k);
//            Location current = location.clone();
//
//            current.add(rightOffset);
//
//            for (float i = 0; i <= 8; i += 0.5f) {
//                current = current.clone().add(direction.clone().multiply(0.5));
//
//                for (LivingEntity livingEntity : current.getNearbyLivingEntities(1)) {
//                    if (!livingEntity.equals(player)) {
//                        Misc.damageNoTicks(livingEntity, 21, player);
//                    }
//                }
//
//                FallingBlock gold = current.getWorld().spawnFallingBlock(current, Material.GOLD_BLOCK.createBlockData());
//                gold.setMetadata("midasStaff", new FixedMetadataValue(plugin, true)); // Add metadata
//                gold.setDropItem(false);
//                gold.setInvulnerable(true);
//                gold.setGravity(true);
//                gold.setCancelDrop(true);
//            }
//        }
//    }
//
//    // Add the new event handler to cancel block placement
//    @EventHandler
//    public void onGoldenBlockLand(EntityChangeBlockEvent event) {
//        if (event.getEntity() instanceof FallingBlock) {
//            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
//            if (fallingBlock.hasMetadata("midasStaff")) {
//                event.setCancelled(true); // Cancel the block placement
//                fallingBlock.remove(); // Remove the FallingBlock entity
//            }
//        }
//    }
//
//    // ... [Rest of the existing code remains unchanged]
//}